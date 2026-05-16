package com.example.scrapbid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.scrapbid.model.Bid;
import com.example.scrapbid.model.Deal;
import com.example.scrapbid.model.Scrap;
import com.example.scrapbid.model.ScrapCategory;
import com.example.scrapbid.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ScrapBid.db";
    private static final int DB_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "phone TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "address TEXT," +
                "created_at TEXT DEFAULT (datetime('now','localtime'))" +
                ")");

        db.execSQL("CREATE TABLE scrap_categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "emoji TEXT NOT NULL," +
                "base_price REAL NOT NULL," +
                "color_hex TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE scraps (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "title TEXT," +
                "weight REAL NOT NULL," +
                "asking_price REAL NOT NULL," +
                "address TEXT NOT NULL," +
                "description TEXT," +
                "status TEXT DEFAULT 'OPEN'," +
                "created_at TEXT DEFAULT (datetime('now','localtime'))," +
                "FOREIGN KEY (user_id) REFERENCES users(id)," +
                "FOREIGN KEY (category_id) REFERENCES scrap_categories(id)" +
                ")");

        db.execSQL("CREATE TABLE bids (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "scrap_id INTEGER NOT NULL," +
                "dealer_id INTEGER NOT NULL," +
                "bid_price REAL NOT NULL," +
                "note TEXT," +
                "status TEXT DEFAULT 'PENDING'," +
                "created_at TEXT DEFAULT (datetime('now','localtime'))," +
                "FOREIGN KEY (scrap_id) REFERENCES scraps(id)," +
                "FOREIGN KEY (dealer_id) REFERENCES users(id)" +
                ")");

        db.execSQL("CREATE TABLE deals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "scrap_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "dealer_id INTEGER NOT NULL," +
                "bid_id INTEGER NOT NULL," +
                "final_price REAL NOT NULL," +
                "status TEXT DEFAULT 'CONFIRMED'," +
                "created_at TEXT DEFAULT (datetime('now','localtime'))," +
                "FOREIGN KEY (scrap_id) REFERENCES scraps(id)," +
                "FOREIGN KEY (user_id) REFERENCES users(id)," +
                "FOREIGN KEY (dealer_id) REFERENCES users(id)," +
                "FOREIGN KEY (bid_id) REFERENCES bids(id)" +
                ")");

        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS deals");
        db.execSQL("DROP TABLE IF EXISTS bids");
        db.execSQL("DROP TABLE IF EXISTS scraps");
        db.execSQL("DROP TABLE IF EXISTS scrap_categories");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[][] cats = {
                {"Iron & Steel", "🔧", "25", "#455A64"},
                {"Copper", "⚡", "450", "#BF360C"},
                {"Aluminium", "🥤", "120", "#1565C0"},
                {"Plastic", "♻️", "10", "#00695C"},
                {"Paper / Cardboard", "📦", "8", "#5D4037"},
                {"E-Waste / Electronics", "💻", "50", "#4527A0"},
                {"Glass", "🪴", "5", "#00838F"},
                {"Brass", "🔩", "300", "#F57F17"},
                {"Batteries", "🔋", "15", "#558B2F"},
                {"Mixed / Others", "🗑️", "12", "#6D4C41"}
        };
        for (String[] c : cats) {
            ContentValues cv = new ContentValues();
            cv.put("name", c[0]);
            cv.put("emoji", c[1]);
            cv.put("base_price", Double.parseDouble(c[2]));
            cv.put("color_hex", c[3]);
            db.insert("scrap_categories", null, cv);
        }
    }

    // USERS

    public long registerUser(String name, String phone, String password, String role, String address) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        cv.put("password", password);
        cv.put("role", role);
        cv.put("address", address);
        return db.insert("users", null, cv);
    }

    public User loginUser(String phone, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM users WHERE phone=? AND password=?",
                new String[]{phone, password});
        User user = null;
        if (c.moveToFirst()) user = cursorToUser(c);
        c.close();
        return user;
    }

    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM users WHERE phone=?", new String[]{phone});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE id=?", new String[]{String.valueOf(id)});
        User user = null;
        if (c.moveToFirst()) user = cursorToUser(c);
        c.close();
        return user;
    }

    private User cursorToUser(Cursor c) {
        User u = new User();
        u.id = c.getInt(c.getColumnIndexOrThrow("id"));
        u.name = c.getString(c.getColumnIndexOrThrow("name"));
        u.phone = c.getString(c.getColumnIndexOrThrow("phone"));
        u.password = c.getString(c.getColumnIndexOrThrow("password"));
        u.role = c.getString(c.getColumnIndexOrThrow("role"));
        u.address = c.getString(c.getColumnIndexOrThrow("address"));
        return u;
    }

    // CATEGORIES

    public List<ScrapCategory> getAllCategories() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM scrap_categories ORDER BY name", null);
        List<ScrapCategory> list = new ArrayList<>();
        while (c.moveToNext()) {
            ScrapCategory cat = new ScrapCategory();
            cat.id = c.getInt(c.getColumnIndexOrThrow("id"));
            cat.name = c.getString(c.getColumnIndexOrThrow("name"));
            cat.emoji = c.getString(c.getColumnIndexOrThrow("emoji"));
            cat.basePricePerKg = c.getDouble(c.getColumnIndexOrThrow("base_price"));
            cat.colorHex = c.getString(c.getColumnIndexOrThrow("color_hex"));
            list.add(cat);
        }
        c.close();
        return list;
    }

    // SCRAPS
    public long addScrap(int userId, int categoryId, String title, double weight,
                         double askingPrice, String address, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("category_id", categoryId);
        cv.put("title", title);
        cv.put("weight", weight);
        cv.put("asking_price", askingPrice);
        cv.put("address", address);
        cv.put("description", description);
        cv.put("status", Scrap.STATUS_OPEN);
        return db.insert("scraps", null, cv);
    }

    public List<Scrap> getScrapsByUser(int userId) {
        return queryScrapList(
                "WHERE s.user_id=?", new String[]{String.valueOf(userId)},
                "ORDER BY s.created_at DESC");
    }

    public List<Scrap> getAvailableScraps(int excludeUserId) {
        return queryScrapList(
                "WHERE s.status IN ('OPEN','BIDDING') AND s.user_id != ?",
                new String[]{String.valueOf(excludeUserId)},
                "ORDER BY s.created_at DESC");
    }

    public Scrap getScrapById(int scrapId) {
        List<Scrap> list = queryScrapList(
                "WHERE s.id=?", new String[]{String.valueOf(scrapId)}, "");
        return list.isEmpty() ? null : list.get(0);
    }

    public int getScrapCount(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM scraps WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getActiveScrapCount(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM scraps WHERE user_id=? AND status IN ('OPEN','BIDDING')",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    private List<Scrap> queryScrapList(String whereClause, String[] args, String orderClause) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT s.*, sc.name AS cat_name, sc.emoji AS cat_emoji, " +
                "sc.base_price AS cat_base_price, sc.color_hex AS cat_color, " +
                "u.name AS user_name, " +
                "(SELECT COUNT(*) FROM bids b WHERE b.scrap_id = s.id) AS bid_count " +
                "FROM scraps s " +
                "JOIN scrap_categories sc ON s.category_id = sc.id " +
                "JOIN users u ON s.user_id = u.id " +
                whereClause + " " + orderClause;
        Cursor c = db.rawQuery(query, args);
        List<Scrap> list = new ArrayList<>();
        while (c.moveToNext()) list.add(cursorToScrap(c));
        c.close();
        return list;
    }

    private Scrap cursorToScrap(Cursor c) {
        Scrap s = new Scrap();
        s.id = c.getInt(c.getColumnIndexOrThrow("id"));
        s.userId = c.getInt(c.getColumnIndexOrThrow("user_id"));
        s.categoryId = c.getInt(c.getColumnIndexOrThrow("category_id"));
        s.title = c.getString(c.getColumnIndexOrThrow("title"));
        s.weight = c.getDouble(c.getColumnIndexOrThrow("weight"));
        s.askingPrice = c.getDouble(c.getColumnIndexOrThrow("asking_price"));
        s.address = c.getString(c.getColumnIndexOrThrow("address"));
        s.description = c.getString(c.getColumnIndexOrThrow("description"));
        s.status = c.getString(c.getColumnIndexOrThrow("status"));
        s.createdAt = c.getString(c.getColumnIndexOrThrow("created_at"));
        s.categoryName = c.getString(c.getColumnIndexOrThrow("cat_name"));
        s.categoryEmoji = c.getString(c.getColumnIndexOrThrow("cat_emoji"));
        s.categoryBasePrice = c.getDouble(c.getColumnIndexOrThrow("cat_base_price"));
        s.categoryColorHex = c.getString(c.getColumnIndexOrThrow("cat_color"));
        s.userName = c.getString(c.getColumnIndexOrThrow("user_name"));
        s.bidCount = c.getInt(c.getColumnIndexOrThrow("bid_count"));
        return s;
    }

    // BIDS

    public long placeBid(int scrapId, int dealerId, double bidPrice, String note) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("scrap_id", scrapId);
            cv.put("dealer_id", dealerId);
            cv.put("bid_price", bidPrice);
            cv.put("note", note);
            cv.put("status", Bid.STATUS_PENDING);
            long bidId = db.insert("bids", null, cv);

            ContentValues scrapUpdate = new ContentValues();
            scrapUpdate.put("status", Scrap.STATUS_BIDDING);
            db.update("scraps", scrapUpdate, "id=? AND status='OPEN'",
                    new String[]{String.valueOf(scrapId)});

            db.setTransactionSuccessful();
            return bidId;
        } finally {
            db.endTransaction();
        }
    }

    public List<Bid> getBidsForScrap(int scrapId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.*, u.name AS dealer_name, u.phone AS dealer_phone " +
                "FROM bids b JOIN users u ON b.dealer_id = u.id " +
                "WHERE b.scrap_id=? ORDER BY b.bid_price DESC",
                new String[]{String.valueOf(scrapId)});
        List<Bid> list = new ArrayList<>();
        while (c.moveToNext()) list.add(cursorToBid(c, false));
        c.close();
        return list;
    }

    public List<Bid> getBidsByDealer(int dealerId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.*, u.name AS dealer_name, u.phone AS dealer_phone, " +
                "s.weight AS scrap_weight, s.address AS scrap_address, s.status AS scrap_status, " +
                "sc.name AS cat_name, sc.emoji AS cat_emoji " +
                "FROM bids b " +
                "JOIN users u ON b.dealer_id = u.id " +
                "JOIN scraps s ON b.scrap_id = s.id " +
                "JOIN scrap_categories sc ON s.category_id = sc.id " +
                "WHERE b.dealer_id=? ORDER BY b.created_at DESC",
                new String[]{String.valueOf(dealerId)});
        List<Bid> list = new ArrayList<>();
        while (c.moveToNext()) list.add(cursorToBid(c, true));
        c.close();
        return list;
    }

    public boolean hasDealerBidOnScrap(int dealerId, int scrapId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM bids WHERE dealer_id=? AND scrap_id=?",
                new String[]{String.valueOf(dealerId), String.valueOf(scrapId)});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public int getDealerBidCount(int dealerId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM bids WHERE dealer_id=?",
                new String[]{String.valueOf(dealerId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    private Bid cursorToBid(Cursor c, boolean withScrapInfo) {
        Bid b = new Bid();
        b.id = c.getInt(c.getColumnIndexOrThrow("id"));
        b.scrapId = c.getInt(c.getColumnIndexOrThrow("scrap_id"));
        b.dealerId = c.getInt(c.getColumnIndexOrThrow("dealer_id"));
        b.bidPrice = c.getDouble(c.getColumnIndexOrThrow("bid_price"));
        b.note = c.getString(c.getColumnIndexOrThrow("note"));
        b.status = c.getString(c.getColumnIndexOrThrow("status"));
        b.createdAt = c.getString(c.getColumnIndexOrThrow("created_at"));
        b.dealerName = c.getString(c.getColumnIndexOrThrow("dealer_name"));
        b.dealerPhone = c.getString(c.getColumnIndexOrThrow("dealer_phone"));
        if (withScrapInfo) {
            b.scrapWeight = c.getDouble(c.getColumnIndexOrThrow("scrap_weight"));
            b.scrapAddress = c.getString(c.getColumnIndexOrThrow("scrap_address"));
            b.scrapStatus = c.getString(c.getColumnIndexOrThrow("scrap_status"));
            b.scrapCategoryName = c.getString(c.getColumnIndexOrThrow("cat_name"));
            b.scrapCategoryEmoji = c.getString(c.getColumnIndexOrThrow("cat_emoji"));
        }
        return b;
    }

    // DEALS

    public long acceptBid(int bidId, int scrapId, int userId, int dealerId, double finalPrice) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues dv = new ContentValues();
            dv.put("scrap_id", scrapId);
            dv.put("user_id", userId);
            dv.put("dealer_id", dealerId);
            dv.put("bid_id", bidId);
            dv.put("final_price", finalPrice);
            dv.put("status", Deal.STATUS_CONFIRMED);
            long dealId = db.insert("deals", null, dv);

            ContentValues acceptCv = new ContentValues();
            acceptCv.put("status", Bid.STATUS_ACCEPTED);
            db.update("bids", acceptCv, "id=?", new String[]{String.valueOf(bidId)});

            ContentValues rejectCv = new ContentValues();
            rejectCv.put("status", Bid.STATUS_REJECTED);
            db.update("bids", rejectCv, "scrap_id=? AND id!=?",
                    new String[]{String.valueOf(scrapId), String.valueOf(bidId)});

            ContentValues scrapCv = new ContentValues();
            scrapCv.put("status", Scrap.STATUS_DEALT);
            db.update("scraps", scrapCv, "id=?", new String[]{String.valueOf(scrapId)});

            db.setTransactionSuccessful();
            return dealId;
        } finally {
            db.endTransaction();
        }
    }

    public List<Deal> getDealsByUser(int userId) {
        return queryDeals("d.user_id=?", new String[]{String.valueOf(userId)});
    }

    public List<Deal> getDealsByDealer(int dealerId) {
        return queryDeals("d.dealer_id=?", new String[]{String.valueOf(dealerId)});
    }

    public int getDealCountByUser(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM deals WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getDealCountByDealer(int dealerId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM deals WHERE dealer_id=?",
                new String[]{String.valueOf(dealerId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    private List<Deal> queryDeals(String where, String[] args) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT d.*, sc.name AS cat_name, sc.emoji AS cat_emoji, sc.color_hex AS cat_color, " +
                "s.weight AS scrap_weight, s.address AS scrap_address, " +
                "u.name AS user_name, dl.name AS dealer_name, dl.phone AS dealer_phone " +
                "FROM deals d " +
                "JOIN scraps s ON d.scrap_id = s.id " +
                "JOIN scrap_categories sc ON s.category_id = sc.id " +
                "JOIN users u ON d.user_id = u.id " +
                "JOIN users dl ON d.dealer_id = dl.id " +
                "WHERE " + where + " ORDER BY d.created_at DESC",
                args);
        List<Deal> list = new ArrayList<>();
        while (c.moveToNext()) {
            Deal deal = new Deal();
            deal.id = c.getInt(c.getColumnIndexOrThrow("id"));
            deal.scrapId = c.getInt(c.getColumnIndexOrThrow("scrap_id"));
            deal.userId = c.getInt(c.getColumnIndexOrThrow("user_id"));
            deal.dealerId = c.getInt(c.getColumnIndexOrThrow("dealer_id"));
            deal.bidId = c.getInt(c.getColumnIndexOrThrow("bid_id"));
            deal.finalPrice = c.getDouble(c.getColumnIndexOrThrow("final_price"));
            deal.status = c.getString(c.getColumnIndexOrThrow("status"));
            deal.createdAt = c.getString(c.getColumnIndexOrThrow("created_at"));
            deal.categoryName = c.getString(c.getColumnIndexOrThrow("cat_name"));
            deal.categoryEmoji = c.getString(c.getColumnIndexOrThrow("cat_emoji"));
            deal.categoryColorHex = c.getString(c.getColumnIndexOrThrow("cat_color"));
            deal.scrapWeight = c.getDouble(c.getColumnIndexOrThrow("scrap_weight"));
            deal.scrapAddress = c.getString(c.getColumnIndexOrThrow("scrap_address"));
            deal.userName = c.getString(c.getColumnIndexOrThrow("user_name"));
            deal.dealerName = c.getString(c.getColumnIndexOrThrow("dealer_name"));
            deal.dealerPhone = c.getString(c.getColumnIndexOrThrow("dealer_phone"));
            list.add(deal);
        }
        c.close();
        return list;
    }
}
