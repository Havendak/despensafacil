package br.com.bsbapps.despensafacil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andre Becklas on 28/11/2016.
 */

public class DatabaseConnector {
    private static final String DATABASE_NAME = "dfdb";
    private static final int DB_CURRENT_VERSION = 1;
    private SQLiteDatabase database;
    private DatabaseOpenHelper dbOpenHelper;

    public DatabaseConnector(Context context){
        /*try{
            context.deleteDatabase(DATABASE_NAME);
        } catch (NullPointerException e){

        }*/
        dbOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DB_CURRENT_VERSION);
    }

    public void open() throws SQLException{
        database = dbOpenHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null) {
            database.close();
        }
    }

    public Long insertList(String name, int defList) {
        ContentValues newList = new ContentValues();
        newList.put("user_list_name", name);
        newList.put("default_list", defList);
        open();
        Long id = database.insert("df_user_list", null, newList);
        close();
        return id;
    }

    public Cursor getAllLists() {
        return database.query("df_user_list", new String[] {"user_list_id", "user_list_name"}, null, null, null, null, "user_list_name");
    }

    public Cursor getList(int id){
        return database.query("df_user_list", null, "user_list_id=" + id, null, null, null, null);
    }

    public Cursor getDefaultList() {
        return database.query("df_user_list", null, "default_list=1", null, null, null, null);
    }

    public void insertProduct(String barcode, String product) {
        ContentValues newProduct = new ContentValues();
        newProduct.put("barcode", barcode);
        newProduct.put("product_name", product);
        newProduct.put("product_status", 0);
        open();
        database.insert("df_product", null, newProduct);
        close();
    }

    public void updateProduct(String barcode, String product) {
        ContentValues newProduct = new ContentValues();
        newProduct.put("product_name", product);
        open();
        database.update("df_product", newProduct, "barcode='" + barcode + "'", null);
        close();
    }

    public Cursor getAllProducts() {
        return database.query("df_product", new String[] {"barcode", "product_name", "product_status"}, null, null, null, null, "product_name");
    }

    public Cursor getProduct(String barcode){
        return database.query("df_product", null, "barcode='" + barcode + "'", null, null, null, null);
    }

    public void insertProductOnList(int list, String barcode, int quantity, Date duedate) {
        ContentValues newListProduct = new ContentValues();
        newListProduct.put("user_list_id", list);
        newListProduct.put("barcode", barcode);
        newListProduct.put("quantity", quantity);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(duedate);
        newListProduct.put("due_date", date);
        open();
        database.insert("df_list_product", null, newListProduct);
        close();

    }

    public Cursor getAllListProducts(int list) {
        String sql = "SELECT A.user_list_id, A.barcode, B.product_name, A.quantity, A.due_date " +
                "FROM df_list_product A INNER JOIN df_product B ON B.barcode = A.barcode " +
                "WHERE A.user_list_id = ? ORDER BY B.product_name";
        return database.rawQuery(sql,new String[]{String.valueOf(list)});
    }

    public Cursor getListProduct(int list, String barcode){
        return database.query("df_list_product", null, "user_list_id=" + list + " AND barcode='" + barcode + "'", null, null, null, null);
    }
}
