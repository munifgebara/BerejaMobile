package br.com.munif.berejamobile;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by munif on 14/05/16.
 */
public class RestAsyncTask<T> extends AsyncTask {

    private RestAsyncTaskListener listener;
    private String endereco;
    private ObjectMapper om;
    private Class clazz;


    public RestAsyncTask(Class clazz,RestAsyncTaskListener listener,String endereco) {
        super();
        this.listener=listener;
        this.endereco=endereco;
        om=new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        om.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        //TODO NÃO Funciona no JAVA 6
        //this.clazz = (Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.clazz = clazz;

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
    public void post(T objeto) {
        this.execute(new RequisicaoPost(objeto));
    }
    public void put(Object id,T objeto) {

        this.execute(new RequisicaoPut(id,objeto));
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
            List<T> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco+id);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.connect();
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Object o = om.readValue(in, clazz);
                resposta.add((T)o);
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
            List<T> resposta=new ArrayList<>();

            URL url=null;
            try {
                url = new URL(endereco);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.connect();
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                resposta = om.readValue(in, om.getTypeFactory().constructCollectionType(List.class, clazz));
                publishProgress("Recuperados " + resposta.size());
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas recuperando " + url.getPath() + "\n" + ex.getLocalizedMessage());
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
            List<T> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco+id);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.setRequestMethod("DELETE");
                urc.connect();
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Object o = om.readValue(in, clazz);
                resposta.add((T)o);
                publishProgress("Excluido "+o);
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas excluindo " + url.getPath() + "\n" + ex.getLocalizedMessage());
            }
            return resposta;
        }
    }
    class RequisicaoPut implements Requisicao{

        private T objeto;
        private Object id;

        public RequisicaoPut(Object id,T objeto){
            this.id=id;
            this.objeto=objeto;
        }

        @Override
        public Object requisita() {
            List<T> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco+id);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.setRequestMethod("PUT");
                urc.connect();
                OutputStream out=urc.getOutputStream();
                out.write(om.writeValueAsBytes(objeto));

                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Object c = om.readValue(in, clazz);
                resposta.add((T)c);
                publishProgress("Alterado "+c);
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas alterando " + url.getPath() + "\n" + ex.getLocalizedMessage());
            }
            return resposta;
        }
    }
    class RequisicaoPost implements Requisicao{

        private T objeto;

        public RequisicaoPost(T objeto){
            this.objeto=objeto;
        }

        @Override
        public Object requisita() {
            List<T> resposta=new ArrayList<>();
            URL url=null;
            try {
                url = new URL(endereco);
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.setRequestMethod("POST");
                urc.connect();
                OutputStream out=urc.getOutputStream();
                out.write(om.writeValueAsBytes(objeto));
                InputStream in=urc.getInputStream();
                publishProgress("Lendo");
                Object c = om.readValue(in, clazz);
                resposta.add((T)c);
                publishProgress("Inserido "+c);
                urc.disconnect();
            }
            catch (Exception ex){
                publishProgress("Problemas inserindo " + url.getPath() + "\n" + ex.getLocalizedMessage());
            }
            return resposta;
        }
    }

}

