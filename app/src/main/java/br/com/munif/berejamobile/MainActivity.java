package br.com.munif.berejamobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,RestAsyncTaskListener{

    private TextView tvStatus;
    private EditText etId;
    private EditText etNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int b : new int[]{R.id.btAlterar, R.id.btConsultar, R.id.btExcluir, R.id.btListar, R.id.btNovo}) {
            ((Button) findViewById(b)).setOnClickListener(this);
        }
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        etId = (EditText) findViewById(R.id.etId);
        etNome = (EditText) findViewById(R.id.etNome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btNovo:
                inserir();
                break;
            case R.id.btListar:
                listar();
                break;
            case R.id.btExcluir:
                excluir();
                break;
            case R.id.btConsultar:
                consultar();
                break;
            case R.id.btAlterar:
                alterar();
                break;
        }
    }


    private void alterar() {
        RestAsyncTask<Cervejaria> rat=new RestAsyncTask<>(Cervejaria.class, this,"http://munif.com.br/bereja/api/cervejaria/");
        Cervejaria cervejaria=new Cervejaria();
        cervejaria.setId(Long.parseLong(etId.getText().toString()));
        cervejaria.setNome(etNome.getText().toString());
        rat.put(cervejaria.getId(),cervejaria);
    }

    private void inserir() {
        RestAsyncTask<Cervejaria> rat=new RestAsyncTask<>(Cervejaria.class,this,"http://munif.com.br/bereja/api/cervejaria/");
        Cervejaria cervejaria=new Cervejaria();
        cervejaria.setNome(etNome.getText().toString());
        rat.post(cervejaria);
    }

    private void listar() {
        RestAsyncTask<Cervejaria> rat=new RestAsyncTask<>(Cervejaria.class,this,"http://munif.com.br/bereja/api/cervejaria/");
        rat.getAll();
    }

    private void consultar(){
        Long id=Long.parseLong(etId.getText().toString());
        RestAsyncTask<Cervejaria> rat=new RestAsyncTask<>(Cervejaria.class,this,"http://munif.com.br/bereja/api/cervejaria/");
        rat.get(id);
    }

    private void excluir(){
        Long id=Long.parseLong(etId.getText().toString());
        RestAsyncTask<Cervejaria> rat=new RestAsyncTask<>(Cervejaria.class,this,"http://munif.com.br/bereja/api/cervejaria/");
        rat.delete(id);
    }


    @Override
    public void onPostExecute(Object o) {
        List<Cervejaria> lista= (List<Cervejaria>) o;
        if (lista.size()>0) {
            etId.setText(lista.get(0).getId().toString());
            etNome.setText(lista.get(0).getNome());
        }
    }

    @Override
    public void onCancelled() {
        tvStatus.setText("Cancelado");
    }

    @Override
    public void onProgressUpdate(Object[] values) {
        tvStatus.setText(values[0].toString());

    }
}
