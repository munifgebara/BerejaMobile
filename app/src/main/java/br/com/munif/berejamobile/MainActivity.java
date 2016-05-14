package br.com.munif.berejamobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, RestAsyncTaskListener, AdapterView.OnItemClickListener {

    private TextView tvStatus;
    private EditText etId;
    private EditText etNome;
    private ListView lvLista;

    private List<Cervejaria> lista;
    private int posicaoNaLista;
    private boolean listando;

    int botoes[] = {R.id.btAlterar, R.id.btExcluir, R.id.btListar, R.id.btNovo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int b : botoes) {
            ((Button) findViewById(b)).setOnClickListener(this);
        }
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        etId = (EditText) findViewById(R.id.etId);
        etNome = (EditText) findViewById(R.id.etNome);
        lvLista = (ListView) findViewById(R.id.lvLista);
        lvLista.setOnItemClickListener(this);
        lista = new ArrayList<>();
        lvLista.setAdapter(new ArrayAdapter<Cervejaria>(
                this, android.R.layout.simple_list_item_1, lista
        ));
        posicaoNaLista = -1;
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
        try {
            habilitaBotoes(false);
            switch (v.getId()) {
                case R.id.btNovo:
                    inserir();
                    break;
                case R.id.btListar:
                    listar();
                    break;
                case R.id.btExcluir:
                    excluir();
                    break;
                case R.id.btAlterar:
                    alterar();
                    break;
            }
        } catch (Exception ex) {
            tvStatus.setText(ex.getLocalizedMessage());
        }
    }


    private void alterar() {
        final Cervejaria cervejaria = new Cervejaria();
        cervejaria.setId(Long.parseLong(etId.getText().toString()));
        cervejaria.setNome(etNome.getText().toString());
        RestAsyncTask<Cervejaria> rat = new RestAsyncTask<Cervejaria>(Cervejaria.class, this, "http://munif.com.br/bereja/api/cervejaria/") {
            @Override
            protected void afterExecute() {
                lista.set(posicaoNaLista, cervejaria);
                ((ArrayAdapter<Cervejaria>) lvLista.getAdapter()).notifyDataSetChanged();
            }
        };
        rat.put(cervejaria.getId(), cervejaria);
    }

    private void inserir() {
        final Cervejaria cervejaria = new Cervejaria();
        cervejaria.setNome(etNome.getText().toString());
        RestAsyncTask<Cervejaria> rat = new RestAsyncTask<Cervejaria>(Cervejaria.class, this, "http://munif.com.br/bereja/api/cervejaria/") {
            @Override
            protected void afterExecute() {
                lista.add(cervejaria);
                ((ArrayAdapter<Cervejaria>) lvLista.getAdapter()).notifyDataSetChanged();
            }
        };
        rat.post(cervejaria);
    }

    private void listar() {
        listando=true;
        RestAsyncTask<Cervejaria> rat = new RestAsyncTask<>(Cervejaria.class, this, "http://munif.com.br/bereja/api/cervejaria/");
        rat.getAll();
    }

    private void consultar() {
        Long id = Long.parseLong(etId.getText().toString());
        RestAsyncTask<Cervejaria> rat = new RestAsyncTask<>(Cervejaria.class, this, "http://munif.com.br/bereja/api/cervejaria/");
        rat.get(id);
    }

    private void excluir() {
        Long id = Long.parseLong(etId.getText().toString());
        RestAsyncTask<Cervejaria> rat = new RestAsyncTask<Cervejaria>(Cervejaria.class, this, "http://munif.com.br/bereja/api/cervejaria/") {
            @Override
            protected void afterExecute() {
                lista.remove(posicaoNaLista);
                ((ArrayAdapter<Cervejaria>) lvLista.getAdapter()).notifyDataSetChanged();
            }
        };
        rat.delete(id);
    }


    @Override
    public void onPostExecute(Object o) {
        if (listando) {
            lista = (List<Cervejaria>) o;
            ((ArrayAdapter<Cervejaria>) lvLista.getAdapter()).notifyDataSetChanged();
            lvLista.setAdapter(new ArrayAdapter<Cervejaria>(
                    this, android.R.layout.simple_list_item_1, lista
            ));
            listando=false;
        }
        habilitaBotoes(true);
    }

    @Override
    public void onCancelled() {
        tvStatus.setText("Cancelado");
    }

    @Override
    public void onProgressUpdate(Object[] values) {
        tvStatus.setText(values[0].toString());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        posicaoNaLista = position;
        etNome.setText(lista.get(position).getNome());
        etId.setText(lista.get(position).getId().toString());
    }

    public void habilitaBotoes(boolean enabled) {
        for (int b : botoes) {
            ((Button) findViewById(b)).setEnabled(enabled);
        }
    }
}
