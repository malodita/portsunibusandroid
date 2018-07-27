package com.malcolm.unibusutilities.entity;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Entity(tableName = "Timetable")
public class Bus {
    @PrimaryKey
    Integer id;
    @ColumnInfo(name = "IMS Eastney (Departures)")
    private
    Integer eastneyD;
    @ColumnInfo(name = "Langstone Campus (for Departures only)")
    private
    Integer langstoneD;
    @ColumnInfo(name = "Locksway Road (for Milton Park)")
    private
    Integer locksway;
    @ColumnInfo(name = "Goldsmith Avenue (adj Lidi)")
    private
    Integer goldsmithAdj;
    @ColumnInfo(name = "Goldsmith Avenue (opp Fratton Station)")
    private
    Integer goldsmithFaw;
    @ColumnInfo(name = "Winston Churchill Avenue (adj Ibis Hotel)")
    private
    Integer winstonIbis;
    @ColumnInfo(name = "Cambridge Road (adj Student Union for Arrivals only)")
    private
    Integer union;
    @ColumnInfo(name = "Cambridge Road (adj Nuffield Building)")
    private
    Integer nuffield;
    @ColumnInfo(name = "Winston Churchill Avenue (adj Law Courts)")
    private
    Integer winstonLaw;
    @ColumnInfo(name = "Goldsmith Avenue (adj Fratton Station)")
    private
    Integer goldsmithFtn;
    @ColumnInfo(name = "Goldsmith Avenue (opp Lidl)")
    private
    Integer goldsmithOpp;
    @ColumnInfo(name = "Goldsmith Avenue (adj Milton Park)")
    private
    Integer milton;
    @ColumnInfo(name = "IMS Eastney")
    private
    Integer eastney;
    @ColumnInfo(name = "Langstone Campus (for Arrivals only)")
    private
    Integer langstone;

    private static final String EASTNEYD = "IMS Eastney (Departures)";
    private static final String LANGSTONED = "Langstone (Departures)";
    private static final String LOCKSWAY = "Locksway Road (for Milton Park)";
    private static final String GOLDSMITHADJ = "Goldsmith Avenue (adj Lidi)";
    private static final String GOLDSMITHFAW = "Goldsmith Avenue (opp Fratton Station)";
    private static final String WINSTONIBIS = "Winston Churchill Avenue (adj Ibis Hotel)";
    private static final String UNION = "Cambridge Road (Student Union)";
    private static final String NUFFIELD = "Cambridge Road (adj Nuffield Building)";
    private static final String WINSTONLAW = "Winston Churchill Avenue (adj Law Courts)";
    private static final String GOLDSMITHFTN = "Goldsmith Avenue (adj Fratton Station";
    private static final String GOLDSMITHOPP = "Goldsmith Avenue (opp Lidl)";
    private static final String MILTON = "Goldsmith Avenue adj Milton Park";
    private static final String EASTNEY = "IMS Eastney";
    private static final String LANGSTONE = "Langstone";


    public Integer get(int stop){
        switch (stop) {
            case 0:
                return eastneyD;
            case 1:
                return langstoneD;
            case 2:
                return locksway;
            case 3:
                return goldsmithAdj;
            case 4:
                return goldsmithFaw;
            case 5:
                return winstonIbis;
            case 6:
                return union;
            case 7:
                return nuffield;
            case 8:
                return winstonLaw;
            case 9:
                return goldsmithFtn;
            case 10:
                return goldsmithOpp;
            case 11:
                return milton;
            case 12:
                return eastney;
            default:
                return langstone;
        }
    }

    public Integer get(String stop){
        switch (stop) {
            case EASTNEYD:
                return eastneyD;
            case LANGSTONED:
                return langstoneD;
            case LOCKSWAY:
                return locksway;
            case GOLDSMITHADJ:
                return goldsmithAdj;
            case GOLDSMITHFAW:
                return goldsmithFaw;
            case WINSTONIBIS:
                return winstonIbis;
            case UNION:
                return nuffield;
            case NUFFIELD:
                return nuffield;
            case WINSTONLAW:
                return winstonLaw;
            case GOLDSMITHFTN:
                return goldsmithFtn;
            case GOLDSMITHOPP:
                return goldsmithOpp;
            case MILTON:
                return milton;
            case EASTNEY:
                return eastneyD;
            default:
                return langstoneD;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEastneyD() {
        return eastneyD;
    }

    public void setEastneyD(Integer eastneyD) {
        this.eastneyD = eastneyD;
    }

    public Integer getLangstoneD() {
        return langstoneD;
    }

    public void setLangstoneD(Integer langstoneD) {
        this.langstoneD = langstoneD;
    }

    public Integer getLocksway() {
        return locksway;
    }

    public void setLocksway(Integer locksway) {
        this.locksway = locksway;
    }

    public Integer getGoldsmithAdj() {
        return goldsmithAdj;
    }

    public void setGoldsmithAdj(Integer goldsmithAdj) {
        this.goldsmithAdj = goldsmithAdj;
    }

    public Integer getGoldsmithFaw() {
        return goldsmithFaw;
    }

    public void setGoldsmithFaw(Integer goldsmithFaw) {
        this.goldsmithFaw = goldsmithFaw;
    }

    public Integer getWinstonIbis() {
        return winstonIbis;
    }

    public void setWinstonIbis(Integer winstonIbis) {
        this.winstonIbis = winstonIbis;
    }

    public Integer getUnion() {
        return union;
    }

    public void setUnion(Integer union) {
        this.union = union;
    }

    public Integer getNuffield() {
        return nuffield;
    }

    public void setNuffield(Integer nuffield) {
        this.nuffield = nuffield;
    }

    public Integer getWinstonLaw() {
        return winstonLaw;
    }

    public void setWinstonLaw(Integer winstonLaw) {
        this.winstonLaw = winstonLaw;
    }

    public Integer getGoldsmithFtn() {
        return goldsmithFtn;
    }

    public void setGoldsmithFtn(Integer goldsmithFtn) {
        this.goldsmithFtn = goldsmithFtn;
    }

    public Integer getGoldsmithOpp() {
        return goldsmithOpp;
    }

    public void setGoldsmithOpp(Integer goldsmithOpp) {
        this.goldsmithOpp = goldsmithOpp;
    }

    public Integer getMilton() {
        return milton;
    }

    public void setMilton(Integer milton) {
        this.milton = milton;
    }

    public Integer getEastney() {
        return eastney;
    }

    public void setEastney(Integer eastney) {
        this.eastney = eastney;
    }

    public Integer getLangstone() {
        return langstone;
    }

    public void setLangstone(Integer langstone) {
        this.langstone = langstone;
    }

    @Dao
    public interface BusDao {

        /**Fetches the raw list of stop times the identified stop has to identify when
         * the next bus is*/
        @Query("select :stopName from Timetable order by id")
        List<Integer> selectStop(String stopName);

        @Query("select * from Timetable order by id")
        List<Bus> getAll();

        @Query("select * from Timetable order by id")
        Cursor getAllCursor();

        @Query("select * from Timetable where id=:busId")
        Cursor getBusDetailCursor(int busId);

        /**Fetches the identified bus and each stop used.*/
        @Query("select * from Timetable where id=:busId")
        LiveData<Bus> getBusDetail(int busId);
    }

}
