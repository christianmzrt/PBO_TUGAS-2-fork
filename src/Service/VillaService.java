package Service;

import Tugas2.DBConnection;
import model.Villa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VillaService {
    public static List<Villa> getAllVillas() throws SQLException {
        List<Villa> villas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM villas";
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Villa villa = mapResultSetToVilla(rs);
                villas.add(villa);
            }
        }

        return villas;
    }

    private static Villa mapResultSetToVilla(ResultSet rs) throws SQLException {
        return new Villa(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("address")
        );
    }
}
