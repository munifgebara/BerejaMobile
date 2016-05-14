package br.com.munif.berejamobile;

public interface RestAsyncTaskListener {
    void onPostExecute(Object o);
    void onCancelled();
    void onProgressUpdate(Object[] values);
}
