package de.lmcstudio.keyall.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.lmcstudio.keyall.LMCKeyall;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseManager {

    private final LMCKeyall plugin;
    private HikariDataSource dataSource;
    private String type;

    public DatabaseManager(LMCKeyall plugin) {
        this.plugin = plugin;
    }

    public void init() {
        FileConfiguration cfg = plugin.getConfig();
        this.type = cfg.getString("database.type", "h2").toLowerCase();

        HikariConfig hc = new HikariConfig();
        hc.setPoolName("LMC-Keyall-Pool");
        hc.setMaximumPoolSize(5);
        hc.setConnectionTimeout(10_000);

        if (type.equals("mysql")) {
            String host = cfg.getString("database.mysql.host", "localhost");
            int port = cfg.getInt("database.mysql.port", 3306);
            String db = cfg.getString("database.mysql.database", "lmc_keyall");
            hc.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db
                    + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8");
            hc.setUsername(cfg.getString("database.mysql.username", "root"));
            hc.setPassword(cfg.getString("database.mysql.password", ""));
        } else {
            String file = cfg.getString("database.h2.file", "keyall");
            hc.setJdbcUrl("jdbc:h2:./plugins/LMC-Keyall/" + file + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
            hc.setUsername("sa");
            hc.setPassword("");
        }

        try {
            this.dataSource = new HikariDataSource(hc);
            createTables();
            plugin.getLogger().info("Database initialized (" + type + ").");
        } catch (Exception e) {
            plugin.getLogger().severe("Could not initialize database: " + e.getMessage());
        }
    }

    private void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS keyall_wins (
                id INT AUTO_INCREMENT PRIMARY KEY,
                player_uuid VARCHAR(36) NOT NULL,
                player_name VARCHAR(16) NOT NULL,
                key_id VARCHAR(64) NOT NULL,
                won_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create tables: " + e.getMessage());
        }
    }

    public void logWin(UUID uuid, String name, String keyId) {
        if (dataSource == null) return;
        String sql = "INSERT INTO keyall_wins (player_uuid, player_name, key_id) VALUES (?, ?, ?)";
        // Async ausführen, um Main-Thread nicht zu blockieren
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setString(3, keyId);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("Could not log win: " + e.getMessage());
            }
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}