package mara.mybox.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mara.mybox.data.MediaInformation;
import mara.mybox.tools.DateTools;
import static mara.mybox.value.AppVariables.logger;

/**
 * @Author Mara
 * @CreateDate 2019-12-8
 * @License Apache License Version 2.0
 */
public class TableMedia extends DerbyBase {

    public TableMedia() {
        Table_Name = "media";
        Keys = new ArrayList<>() {
            {
                add("address");
            }
        };
        Create_Table_Statement
                = " CREATE TABLE media ( "
                + "  address VARCHAR(32672)  NOT NULL, "
                + "  video_encoding VARCHAR(1024)  , "
                + "  audio_encoding VARCHAR(1024)  , "
                + "  duration BIGINT, "
                + "  size BIGINT, "
                + "  width INT, "
                + "  height INT, "
                + "  info VARCHAR(32672), "
                + "  html CLOB, "
                + "  modify_time TIMESTAMP NOT NULL, "
                + "  PRIMARY KEY (address)"
                + " )";
    }

    public static MediaInformation read(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }
        try ( Connection conn = DriverManager.getConnection(protocol + dbHome() + login);
                 Statement statement = conn.createStatement()) {
            statement.setMaxRows(1);
            String sql = " SELECT * FROM media WHERE address='" + address + "'";
            ResultSet results = statement.executeQuery(sql);
            if (results.next()) {
                MediaInformation media = new MediaInformation(address);
                media.setVideoEncoding(results.getString("video_encoding"));
                media.setAudioEncoding(results.getString("audio_encoding"));
                media.setFileSize(results.getLong("size"));
                media.setDuration(results.getLong("duration"));
                media.setWidth(results.getInt("width"));
                media.setHeight(results.getInt("height"));
                media.setInfo(results.getString("info"));
                media.setHtml(results.getString("html"));
                return media;
            } else {
                return null;
            }

        } catch (Exception e) {
            failed(e);
//            // logger.debug(e.toString());
            return null;
        }
    }

    public static List<MediaInformation> read(List<String> addresses) {
        List<MediaInformation> medias = new ArrayList();
        if (addresses == null || addresses.isEmpty()) {
            return medias;
        }
        try ( Connection conn = DriverManager.getConnection(protocol + dbHome() + login);
                 Statement statement = conn.createStatement()) {
            medias = read(statement, addresses);
        } catch (Exception e) {
            failed(e);
//            // logger.debug(e.toString());
        }
        return medias;
    }

    public static List<MediaInformation> read(Statement statement,
            List<String> addresses) {
        List<MediaInformation> medias = new ArrayList();
        if (statement == null || addresses == null || addresses.isEmpty()) {
            return medias;
        }
        try {
            String inStr = "( '" + addresses.get(0) + "'";
            for (int i = 1; i < addresses.size(); ++i) {
                inStr += ", '" + addresses.get(i) + "'";
            }
            inStr += " )";
            String sql = " SELECT * FROM media WHERE address IN " + inStr;
            ResultSet results = statement.executeQuery(sql);
            while (results.next()) {
                MediaInformation media = new MediaInformation(results.getString("address"));
                media.setVideoEncoding(results.getString("video_encoding"));
                media.setAudioEncoding(results.getString("audio_encoding"));
                media.setFileSize(results.getLong("size"));
                media.setDuration(results.getLong("duration"));
                media.setWidth(results.getInt("width"));
                media.setHeight(results.getInt("height"));
                media.setInfo(results.getString("info"));
                media.setHtml(results.getString("html"));
                medias.add(media);
            }
        } catch (Exception e) {
            failed(e);
//            // logger.debug(e.toString());
        }
        return medias;
    }

    public static boolean write(MediaInformation media) {
        if (media == null || media.getAddress() == null) {
            return false;
        }
        try ( Connection conn = DriverManager.getConnection(protocol + dbHome() + login);
                 Statement statement = conn.createStatement()) {
            String sql = "DELETE FROM media WHERE address='" + media.getAddress() + "'";
            statement.executeUpdate(sql);
            sql = "INSERT INTO media(address,video_encoding,audio_encoding,duration,size,width,height,info,html,modify_time) VALUES('"
                    + media.getAddress() + "', '" + media.getVideoEncoding() + "', '" + media.getAudioEncoding() + "', "
                    + media.getDuration() + ", " + media.getFileSize() + ", " + media.getWidth() + ", "
                    + media.getHeight() + ", '" + media.getInfo() + "', '" + media.getHtml() + "', '"
                    + DateTools.datetimeToString(new Date()) + "')";
            statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            failed(e);
//            logger.debug(e.toString());
            return false;
        }
    }

    public static boolean write(List<MediaInformation> medias) {
        if (medias == null || medias.isEmpty()) {
            return false;
        }
        try ( Connection conn = DriverManager.getConnection(protocol + dbHome() + login);
                 Statement statement = conn.createStatement()) {
            return write(statement, medias);
        } catch (Exception e) {
            failed(e);
//            logger.debug(e.toString());
            return false;
        }
    }

    public static boolean write(Statement statement,
            List<MediaInformation> medias) {
        if (statement == null || medias == null || medias.isEmpty()) {
            return false;
        }
        try {
            String sql;
//            conn.setAutoCommit(false);
            for (MediaInformation media : medias) {
                sql = "DELETE FROM media WHERE address='" + media.getAddress() + "'";
                statement.executeUpdate(sql);
                sql = "INSERT INTO media(address,video_encoding,audio_encoding,duration,size,width,height,info,html,modify_time) VALUES('"
                        + media.getAddress() + "', '" + media.getVideoEncoding() + "', '" + media.getAudioEncoding() + "', "
                        + media.getDuration() + ", " + media.getFileSize() + ", " + media.getWidth() + ", "
                        + media.getHeight() + ", '" + media.getInfo() + "', '" + media.getHtml() + "', '"
                        + DateTools.datetimeToString(new Date()) + "')";
                statement.executeUpdate(sql);
            }
//            conn.commit();
            return true;
        } catch (Exception e) {
            failed(e);
            logger.debug(e.toString());
            return false;
        }
    }

    public static boolean delete(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return true;
        }
        try ( Connection conn = DriverManager.getConnection(protocol + dbHome() + login);
                 Statement statement = conn.createStatement()) {
            return delete(statement, addresses);
        } catch (Exception e) {
            failed(e);
//            logger.debug(e.toString());
            return false;
        }
    }

    public static boolean delete(Statement statement, List<String> addresses) {
        if (statement == null || addresses == null || addresses.isEmpty()) {
            return true;
        }
        try {
            String inStr = "( '" + addresses.get(0) + "'";
            for (int i = 1; i < addresses.size(); ++i) {
                inStr += ", '" + addresses.get(i) + "'";
            }
            inStr += " )";
            String sql = "DELETE FROM media WHERE address IN " + inStr;
            statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            failed(e);
//            logger.debug(e.toString());
            return false;
        }
    }

    public static boolean delete(String address) {
        try ( Connection conn = DriverManager.getConnection(protocol + dbHome() + login);
                 Statement statement = conn.createStatement()) {
            String sql = "DELETE FROM media WHERE address='" + address + "'";
            statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            failed(e);
//            logger.debug(e.toString());
            return false;
        }
    }

}
