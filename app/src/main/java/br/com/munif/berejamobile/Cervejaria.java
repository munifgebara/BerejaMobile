package br.com.munif.berejamobile;

/**
 * Created by munif on 14/05/16.
 */
public class Cervejaria {

    private Long id;
    private String nome;

    public Cervejaria() {
        id=System.currentTimeMillis()*1000;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Cervejaria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
