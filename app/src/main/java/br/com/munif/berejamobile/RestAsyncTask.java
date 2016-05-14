package br.com.munif.berejamobile;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by munif on 14/05/16.
 */
public class RestAsyncTask extends AsyncTask {

    private RestAsyncTaskListener listener;
    private String endereco;
    private ObjectMapper om;


    public RestAsyncTask(RestAsyncTaskListener listener,String endereco) {
        super();
        this.listener=listener;
        this.endereco=endereco;
        om=new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        om.enable(DeserializationFeature.USE_LONG_FOR_INTS);
    }


    @Override
    protected Object doInBackground(Object[] params) {
        publishProgress("Inicio");
        Requisicao requisicao= (Requisicao) params[0];
        return requisicao.requisita();
    }


    @Override
    protected void onPostExecute(Object o) {
        listener.onPostExecute(o);
    }

    @Override
    protected void onCancelled() {
        listener.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        listener.onProgressUpdate(values);
    }

    public void get(Long id) {
        this.execute(new RequisicaoGetOne(id));
    }

    public void getAll() {
        this.execute(new RequisicaoGetAll());
    }
    public void post(Cervejaria cervejaria) {
        this.execute(new RequisicaoPost(cervejaria));
    }
    public void put(Cervejaria cervejaria) {

        this.execute(new RequisicaoPut(cervejaria));
    }
    public void delete(Long id) {
        this.execute(new RequisicaoDelete(id));
    }



    interface Requisicao{

        Object requisita();
    }

    class RequisicaoGetOne implements Requisicao{

        private Object id;

        public RequisicaoGetOne(Object id){
            this.id=id;
        }

        @Override
        public Object requisita() {
            List<Cervejaria> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco+id);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.connect();
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Cervejaria cervejaria = om.readValue(in, Cervejaria.class);
                resposta.add(cervejaria);
                publishProgress("Fim");
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas recuperando " + url.getPath() + "\n" + ex.toString());
            }
            return resposta;
        }
    }
    class RequisicaoGetAll implements Requisicao{

        public RequisicaoGetAll(){

        }

        @Override
        public Object requisita() {
            List<Cervejaria> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.connect();
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Cervejaria[] cervejarias = om.readValue(in, Cervejaria[].class);
                resposta.addAll(Arrays.asList(cervejarias));
                publishProgress("Recuperados "+resposta.size());
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas recuperando " + url.getPath() + "\n" + ex.toString());
            }
            return resposta;
        }
    }
    class RequisicaoDelete implements Requisicao{

        private Object id;

        public RequisicaoDelete(Object id){
            this.id=id;
        }

        @Override
        public Object requisita() {
            List<Cervejaria> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco+id);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.setRequestMethod("DELETE");
                urc.connect();
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Cervejaria cervejaria = om.readValue(in, Cervejaria.class);
                resposta.add(cervejaria);
                publishProgress("Excluido "+cervejaria.getNome());
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas recuperando " + url.getPath() + "\n" + ex.toString());
            }
            return resposta;
        }
    }
    class RequisicaoPut implements Requisicao{

        private Cervejaria cervejaria;

        public RequisicaoPut(Cervejaria cervejaria){
            this.cervejaria=cervejaria;
        }

        @Override
        public Object requisita() {
            List<Cervejaria> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco+cervejaria.getId());
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.setRequestMethod("PUT");
                urc.connect();
                OutputStream out=urc.getOutputStream();
                out.write(om.writeValueAsBytes(cervejaria));

                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Cervejaria c = om.readValue(in, Cervejaria.class);
                resposta.add(c);
                publishProgress("Alterado "+c.getId());
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas recuperando " + url.getPath() + "\n" + ex.toString());
            }
            return resposta;
        }
    }
    class RequisicaoPost implements Requisicao{

        private Cervejaria cervejaria;

        public RequisicaoPost(Cervejaria cervejaria){
            this.cervejaria=cervejaria;
        }

        @Override
        public Object requisita() {
            List<Cervejaria> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.setRequestMethod("POST");
                urc.connect();
                OutputStream out=urc.getOutputStream();
                out.write(om.writeValueAsBytes(cervejaria));
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Cervejaria c = om.readValue(in, Cervejaria.class);
                resposta.add(c);
                publishProgress("Inserido "+c.getId());
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas recuperando " + url.getPath() + "\n" + ex.toString());
            }
            return resposta;
        }
    }

}

