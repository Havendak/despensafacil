package br.com.bsbapps.despensafacil.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import br.com.bsbapps.despensafacil.DatabaseOpenHelper;

/**
 * Created by proca on 02/12/2016.
 * Repesenta uma lista co itens da despensa, PantryItem
 */

public class PantryList {
    // Database
    private static SQLiteDatabase database;
    private DatabaseOpenHelper dbHelper;

    // Colunas
    private String[] allColumns = {DatabaseOpenHelper.COLUMN_LIST_ID,
            DatabaseOpenHelper.COLUMN_LIST_NAME,
            DatabaseOpenHelper.COLUMN_STATUS};

    // Variáveis privadas
    private int listId;
    private String listName;
    private int status;

    // Constantes
    public static final int PANTRY_LIST_STATUS_NORMAL = 0;
    public static final int PANTRY_LIST_STATUS_DEFAULT = 1;
    public static final int PANTRY_LIST_STATUS_INACTIVE = 2;

    // Construtor
    public PantryList(Context context) {
        dbHelper = new DatabaseOpenHelper(context);
    }

    // Métodos de definição e obtenção das variáveis privadas
    public int getListId() {
        return listId;
    }

    public void setListId(int ListId) {
        this.listId = ListId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {  this.status = status; }

    private void clearFields() {
        listId=0;
        listName="";
        status=0;
    }

    // Método de abertura do database
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Metodo de fechamento do database
    public void close() {
        dbHelper.close();
    }

    //Método de inserção
    public long insert(){
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_LIST_NAME, listName);
        values.put(DatabaseOpenHelper.COLUMN_STATUS, status);
        open();
        long id = database.insert(DatabaseOpenHelper.TABLE_PANTRY_LIST, null, values);
        close();
        setListId((int) id);
        return id;

    }

    //Método de deleção
    public void delete() {
        database.delete(DatabaseOpenHelper.TABLE_PANTRY_LIST, DatabaseOpenHelper.COLUMN_LIST_ID
                + " = " + listId, null);
        clearFields();
    }

    //Retorna a lista de despensa padrão (Status=1)
    public Cursor getDefaultList() {
        open();
        Cursor c = database.query(DatabaseOpenHelper.TABLE_PANTRY_LIST, null,
                DatabaseOpenHelper.COLUMN_STATUS + "=" + PANTRY_LIST_STATUS_DEFAULT,
                null, null, null, null);
        close();
        return c;
    }

    // Retorna todos os produtos da lista
    public Cursor getProducts() {
        String sql = "SELECT A." + DatabaseOpenHelper.COLUMN_ID +
                ", A." + DatabaseOpenHelper.COLUMN_LIST_ID +
                ", A." + DatabaseOpenHelper.COLUMN_BARCODE +
                ", B." + DatabaseOpenHelper.COLUMN_PRODUCT_NAME +
                ", A." + DatabaseOpenHelper.COLUMN_QUANTITY +
                ", A." + DatabaseOpenHelper.COLUMN_DUE_DATE +
                " FROM " + DatabaseOpenHelper.TABLE_PANTRY_ITEM +
                " A INNER JOIN " + DatabaseOpenHelper.TABLE_PRODUCT +
                " B ON B." + DatabaseOpenHelper.COLUMN_BARCODE +
                " = A." + DatabaseOpenHelper.COLUMN_BARCODE +
                " WHERE A." + DatabaseOpenHelper.COLUMN_LIST_ID +
                "= ? ORDER BY B." + DatabaseOpenHelper.COLUMN_PRODUCT_NAME;
        return database.rawQuery(sql,new String[]{String.valueOf(listId)});
    }
}
