package Service;

import Tugas2.DBConnection;
import model.Roomtype;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeService {

    public static List<Roomtype> getAllRoomTypes(int id) throws SQLException {
        List<Roomtype> roomTypes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM room_types WHERE villa="+ id;
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Roomtype roomType = mapResultSetToRoomtype(rs);
                roomTypes.add(roomType);
            }
        }

        return roomTypes;
    }

    private static Roomtype mapResultSetToRoomtype(ResultSet rs) throws SQLException {
        Roomtype roomtype = new Roomtype();
        roomtype.setId(rs.getInt("id"));
        roomtype.setVilla(rs.getInt("villa"));
        roomtype.setName(rs.getString("name"));
        roomtype.setQuantity(rs.getInt("quantity"));
        roomtype.setCapacity(rs.getInt("capacity"));
        roomtype.setPrice(rs.getInt("price"));
        roomtype.setBedSize(rs.getString("bed_size"));
        roomtype.setHasDesk(rs.getBoolean("has_desk"));
        roomtype.setHasAc(rs.getBoolean("has_ac"));
        roomtype.setHasTv(rs.getBoolean("has_tv"));
        roomtype.setHasWifi(rs.getBoolean("has_wifi"));
        roomtype.setHasShower(rs.getBoolean("has_shower"));
        roomtype.setHasHotwater(rs.getBoolean("has_hotwater"));
        roomtype.setHasFridge(rs.getBoolean("has_fridge"));
        return roomtype;
    }
}
