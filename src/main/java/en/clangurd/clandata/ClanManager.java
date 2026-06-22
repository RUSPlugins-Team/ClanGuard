package en.clangurd.clandata;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;
import en.clangurd.lang.Lang;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClanManager {

    private static final String LEGACY_FOLDER = "plugins/clanguard";
    private static final int MAX_PLAYERS = 150;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Map<String, String[]> pendingInvites = new HashMap<>();
    private static final Map<String, String[]> lastChatMessages = new HashMap<>();

    private static Plugin plugin;
    private static boolean databaseEnabled;
    private static String databaseType;
    private static Connection connection;

    public static final class HomePoint {
        private final String name;
        private final String level;
        private final double x;
        private final double y;
        private final double z;

        public HomePoint(String name, String level, double x, double y, double z) {
            this.name = name;
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String getName() {
            return name;
        }

        public String getLevel() {
            return level;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }

    private ClanManager() {
    }

    public static void init(Plugin owner) {
        plugin = owner;
        databaseEnabled = false;
        databaseType = "FILE";

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        Config cfg = new Config(new File(dataFolder, "database.json"), Config.JSON);
        if (!cfg.getBoolean("enabled", true)) {
            Server.getInstance().getLogger().info("[ClanGuard] Database is disabled in database.json. Using file storage.");
            return;
        }

        String type = cfg.getString("type", "RUSPluginsSQL").toUpperCase();
        String url;
        String username = cfg.getString("username", "");
        String password = cfg.getString("password", "");

        Server.getInstance().getLogger().info("[ClanGuard] Initializing database connection. Requested type: " + type);

        try {
            if ("MYSQL".equals(type)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                url = "jdbc:mysql://" + cfg.getString("host", "127.0.0.1") + ":" + cfg.getInt("port", 3306) + "/" +
                        cfg.getString("database", "clanguard") + "?useSSL=false&autoReconnect=true&serverTimezone=UTC";
                connection = DriverManager.getConnection(url, username, password);
                Server.getInstance().getLogger().info("[ClanGuard] Connected to MySQL at " + cfg.getString("host", "127.0.0.1") + ":" + cfg.getInt("port", 3306));
            } else if ("MARIADB".equals(type)) {
                Class.forName("org.mariadb.jdbc.Driver");
                url = "jdbc:mariadb://" + cfg.getString("host", "127.0.0.1") + ":" + cfg.getInt("port", 3306) + "/" +
                        cfg.getString("database", "clanguard");
                connection = DriverManager.getConnection(url, username, password);
                Server.getInstance().getLogger().info("[ClanGuard] Connected to MariaDB at " + cfg.getString("host", "127.0.0.1") + ":" + cfg.getInt("port", 3306));
            } else if ("SQLITE3".equals(type)) {
                Class.forName("org.sqlite.JDBC");
                File sqlite = new File(dataFolder, "clanguard.db");
                url = "jdbc:sqlite:" + sqlite.getAbsolutePath();
                connection = DriverManager.getConnection(url);
                Server.getInstance().getLogger().info("[ClanGuard] Connected to SQLite3 at " + sqlite.getAbsolutePath());
            } else {
                Class.forName("org.sqlite.JDBC");
                File sqlite = new File(dataFolder, "ruspluginssql.db");
                url = "jdbc:sqlite:" + sqlite.getAbsolutePath();
                connection = DriverManager.getConnection(url);
                type = "RUSPLUGINSSQL";
                Server.getInstance().getLogger().info("[ClanGuard] Connected to RUSPluginsSQL at " + sqlite.getAbsolutePath());
            }

            databaseType = type;
            databaseEnabled = true;
            createDatabaseSchema();
            migrateLegacyFilesToDatabase();
            Server.getInstance().getLogger().info("[ClanGuard] Database initialization complete. Active backend: " + databaseType);
        } catch (ClassNotFoundException | SQLException e) {
            databaseEnabled = false;
            databaseType = "FILE";
            Server.getInstance().getLogger().warning("[ClanGuard] Database connection failed: " + e.getMessage());
            Server.getInstance().getLogger().warning("[ClanGuard] Fallback activated. Using file storage backend.");
        }
    }

    private static void createDatabaseSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS clan_storage (" +
                    "clan_name VARCHAR(64) NOT NULL," +
                    "file_name VARCHAR(64) NOT NULL," +
                    "content TEXT NOT NULL," +
                    "hash VARCHAR(128) NOT NULL," +
                    "PRIMARY KEY (clan_name, file_name))");
        }
    }

    private static void migrateLegacyFilesToDatabase() {
        if (!databaseEnabled) {
            return;
        }

        File root = new File(LEGACY_FOLDER);
        if (!root.exists()) {
            return;
        }

        File[] clans = root.listFiles(File::isDirectory);
        if (clans == null) {
            return;
        }

        int migrated = 0;
        for (File clanDir : clans) {
            File[] files = clanDir.listFiles(File::isFile);
            if (files == null) {
                continue;
            }
            for (File file : files) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    if (!hasDatabaseFile(clanDir.getName(), file.getName())) {
                        writeDatabaseFile(clanDir.getName(), file.getName(), content);
                        migrated++;
                    }
                } catch (IOException ignored) {
                }
            }
        }

        if (migrated > 0) {
            Server.getInstance().getLogger().info("[ClanGuard] Migrated " + migrated + " legacy files into database storage");
        }
    }

    private static boolean hasDatabaseFile(String clanName, String fileName) {
        String sql = "SELECT 1 FROM clan_storage WHERE clan_name = ? AND file_name = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, clanName);
            ps.setString(2, fileName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static List<String> listClans() {
        if (databaseEnabled) {
            List<String> clans = new ArrayList<>();
            String sql = "SELECT DISTINCT clan_name FROM clan_storage";
            try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    clans.add(rs.getString(1));
                }
            } catch (SQLException ignored) {
            }
            return clans;
        }

        File root = new File(LEGACY_FOLDER);
        List<String> clans = new ArrayList<>();
        File[] dirs = root.listFiles(File::isDirectory);
        if (dirs != null) {
            for (File dir : dirs) {
                clans.add(dir.getName());
            }
        }
        return clans;
    }

    private static String readStorageFile(String clanName, String fileName) {
        if (databaseEnabled) {
            String sql = "SELECT content, hash FROM clan_storage WHERE clan_name = ? AND file_name = ? LIMIT 1";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, clanName);
                ps.setString(2, fileName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    String content = rs.getString("content");
                    String hash = rs.getString("hash");
                    if (!sha256(content).equalsIgnoreCase(hash)) {
                        Server.getInstance().getLogger().warning("[ClanGuard] Hash mismatch in database for " + clanName + "/" + fileName);
                    }
                    return content;
                }
            } catch (SQLException e) {
                return null;
            }
        }

        File file = new File(LEGACY_FOLDER + "/" + clanName, fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            verifyFileHash(clanName, fileName, content);
            return content;
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean writeStorageFile(String clanName, String fileName, String content) {
        if (databaseEnabled) {
            return writeDatabaseFile(clanName, fileName, content);
        }

        File dir = new File(LEGACY_FOLDER, clanName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            updateFileHash(clanName, fileName, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean writeDatabaseFile(String clanName, String fileName, String content) {
        String sql;
        if ("MYSQL".equals(databaseType) || "MARIADB".equals(databaseType)) {
            sql = "INSERT INTO clan_storage (clan_name, file_name, content, hash) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE content = VALUES(content), hash = VALUES(hash)";
        } else {
            sql = "INSERT INTO clan_storage (clan_name, file_name, content, hash) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT(clan_name, file_name) DO UPDATE SET content = excluded.content, hash = excluded.hash";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, clanName);
            ps.setString(2, fileName);
            ps.setString(3, content);
            ps.setString(4, sha256(content));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean deleteClanStorage(String clanName) {
        if (databaseEnabled) {
            String sql = "DELETE FROM clan_storage WHERE clan_name = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, clanName);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        File dir = new File(LEGACY_FOLDER, clanName);
        if (!dir.exists()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        return dir.delete();
    }

    private static void verifyFileHash(String clanName, String fileName, String content) {
        File hashesFile = new File(LEGACY_FOLDER + "/" + clanName, "hashes.json");
        if (!hashesFile.exists()) {
            return;
        }
        Config cfg = new Config(hashesFile, Config.JSON);
        String expected = cfg.getString(fileName, "");
        if (expected.isEmpty()) {
            return;
        }
        String current = sha256(content);
        if (!expected.equalsIgnoreCase(current)) {
            Server.getInstance().getLogger().warning("[ClanGuard] Hash mismatch for file " + clanName + "/" + fileName);
        }
    }

    private static void updateFileHash(String clanName, String fileName, String content) {
        File hashesFile = new File(LEGACY_FOLDER + "/" + clanName, "hashes.json");
        Config cfg = new Config(hashesFile, Config.JSON);
        cfg.set(fileName, sha256(content));
        cfg.save();
    }

    private static String sha256(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static boolean clanExists(String clanName) {
        return listClans().stream().anyMatch(name -> name.equalsIgnoreCase(clanName));
    }

    public static boolean tagExists(String tag) {
        for (String clan : listClans()) {
            String info = readStorageFile(clan, "infoclan.db");
            if (info != null && info.contains("\"tag\":\"" + tag + "\"")) {
                return true;
            }
        }
        return false;
    }

    public static String getPlayerClan(String playerName) {
        for (String clan : listClans()) {
            String players = readStorageFile(clan, "players.db");
            if (players != null && players.contains("\"nick\": \"" + playerName + "\"")) {
                return clan;
            }
        }
        return null;
    }

    public static boolean isPlayerLeader(String playerName, String clanName) {
        String info = readStorageFile(clanName, "infoclan.db");
        return info != null && info.contains("\"leader\": \"" + playerName + "\"");
    }

    public static String getPlayerRole(String playerName, String clanName) {
        String content = readStorageFile(clanName, "players.db");
        if (content == null) {
            return "member";
        }
        int nickIndex = content.indexOf("\"nick\": \"" + playerName + "\"");
        if (nickIndex == -1) {
            return "member";
        }
        int roleIndex = content.indexOf("\"role\": \"", nickIndex);
        if (roleIndex == -1) {
            return "member";
        }
        roleIndex += 9;
        int roleEnd = content.indexOf("\"", roleIndex);
        return roleEnd == -1 ? "member" : content.substring(roleIndex, roleEnd);
    }

    public static Map<String, Object> getClanInfo(String clanName) {
        String infoContent = readStorageFile(clanName, "infoclan.db");
        String playersContent = readStorageFile(clanName, "players.db");
        if (infoContent == null || playersContent == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("name", extractValue(infoContent, "name"));
        info.put("tag", extractValue(infoContent, "tag"));
        info.put("leader", extractValue(infoContent, "leader"));
        info.put("createdAt", extractValue(infoContent, "createdAt"));
        info.put("treasury", extractLongValue(infoContent, "treasury"));
        info.put("level", extractIntValue(infoContent, "level"));
        info.put("description", extractValueOrDefault(infoContent, "description", "No description"));
        info.put("rank", extractValueOrDefault(infoContent, "rank", "None"));
        info.put("title", extractValueOrDefault(infoContent, "title", "None"));
        info.put("regions", extractIntValueOrDefault(infoContent, "regions", 0));
        info.put("homePoints", getHomePointsSummary(clanName));
        info.put("wins", extractIntValueOrDefault(infoContent, "wins", 0));
        info.put("losses", extractIntValueOrDefault(infoContent, "losses", 0));
        info.put("deaths", extractIntValueOrDefault(infoContent, "deaths", 0));
        info.put("maxPlayers", extractIntValue(playersContent, "maxPlayers"));
        info.put("count", extractIntValue(playersContent, "count"));
        return info;
    }

    public static long getClanTreasury(String clanName) {
        String info = readStorageFile(clanName, "infoclan.db");
        return info == null ? 0 : extractLongValue(info, "treasury");
    }

    public static double calculateCommission(long amount) {
        double percentage = 1.0 + (Math.log10(amount / 10000.0) * 3.0);
        percentage = Math.max(1.0, Math.min(10.0, percentage));
        return percentage / 100.0;
    }

    public static boolean depositToTreasury(String clanName, String playerName, long amount) {
        String info = readStorageFile(clanName, "infoclan.db");
        if (info == null) {
            return false;
        }
        long currentTreasury = extractLongValue(info, "treasury");
        double commission = calculateCommission(amount);
        long commissionAmount = (long) (amount * commission);
        long depositAmount = amount - commissionAmount;
        long newTreasury = currentTreasury + depositAmount;
        info = info.replaceAll("\"treasury\": \\d+", "\"treasury\": " + newTreasury);
        return writeStorageFile(clanName, "infoclan.db", info);
    }

    public static boolean withdrawFromTreasury(String clanName, String playerName, long amount) {
        String info = readStorageFile(clanName, "infoclan.db");
        if (info == null) {
            return false;
        }
        long currentTreasury = extractLongValue(info, "treasury");
        if (amount > currentTreasury) {
            return false;
        }
        long newTreasury = currentTreasury - amount;
        info = info.replaceAll("\"treasury\": \\d+", "\"treasury\": " + newTreasury);
        return writeStorageFile(clanName, "infoclan.db", info);
    }

    public static boolean createHomePoint(String clanName, String homeName, Player player) {
        String homesContent = readStorageFile(clanName, "homes.db");
        List<String> lines = new ArrayList<>();
        if (homesContent != null && !homesContent.trim().isEmpty()) {
            for (String line : homesContent.split("\\n")) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }

        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length >= 1 && parts[0].equalsIgnoreCase(homeName)) {
                return false;
            }
        }

        String record = homeName + "|" + player.getLevel().getName() + "|" +
                player.getX() + "|" + player.getY() + "|" + player.getZ();
        lines.add(record);
        String newContent = String.join("\n", lines);
        if (!newContent.endsWith("\n")) {
            newContent = newContent + "\n";
        }

        boolean ok = writeStorageFile(clanName, "homes.db", newContent);
        if (ok) {
            updateHomePointsField(clanName);
        }
        return ok;
    }

    public static List<String> getHomePointNames(String clanName) {
        String homesContent = readStorageFile(clanName, "homes.db");
        List<String> names = new ArrayList<>();
        if (homesContent == null || homesContent.trim().isEmpty()) {
            return names;
        }
        for (String line : homesContent.split("\\n")) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length >= 1) {
                names.add(parts[0]);
            }
        }
        return names;
    }

    public static HomePoint getHomePoint(String clanName, String homeName) {
        String homesContent = readStorageFile(clanName, "homes.db");
        if (homesContent == null) {
            return null;
        }
        for (String line : homesContent.split("\\n")) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) {
                continue;
            }
            if (!parts[0].equalsIgnoreCase(homeName)) {
                continue;
            }
            try {
                return new HomePoint(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static String getHomePointsSummary(String clanName) {
        List<String> names = getHomePointNames(clanName);
        if (names.isEmpty()) {
            return "None";
        }
        return String.join(", ", names);
    }

    private static void updateHomePointsField(String clanName) {
        String info = readStorageFile(clanName, "infoclan.db");
        if (info == null) {
            return;
        }
        String summary = getHomePointsSummary(clanName).replace("\"", "");
        if (info.contains("\"homePoints\":")) {
            info = info.replaceAll("\"homePoints\": \"[^\"]*\"", "\"homePoints\": \"" + summary + "\"");
        } else {
            info = info.replace("\n}", ",\n  \"homePoints\": \"" + summary + "\"\n}");
        }
        writeStorageFile(clanName, "infoclan.db", info);
    }

    public static boolean transferOwnership(String clanName, String oldLeader, String newLeader) {
        String info = readStorageFile(clanName, "infoclan.db");
        String players = readStorageFile(clanName, "players.db");
        if (info == null || players == null) {
            return false;
        }
        if (!info.contains("\"leader\": \"" + oldLeader + "\"")) {
            return false;
        }
        if (!players.contains("\"nick\": \"" + newLeader + "\"")) {
            return false;
        }

        info = info.replace("\"leader\": \"" + oldLeader + "\"", "\"leader\": \"" + newLeader + "\"");
        players = replaceRoleForNick(players, oldLeader, "member");
        players = replaceRoleForNick(players, newLeader, "leader");
        return writeStorageFile(clanName, "infoclan.db", info) && writeStorageFile(clanName, "players.db", players);
    }

    private static String replaceRoleForNick(String content, String nick, String role) {
        int nickIndex = content.indexOf("\"nick\": \"" + nick + "\"");
        if (nickIndex == -1) {
            return content;
        }
        int roleIndex = content.indexOf("\"role\": \"", nickIndex);
        if (roleIndex == -1) {
            return content;
        }
        int roleStart = roleIndex + 9;
        int roleEnd = content.indexOf("\"", roleStart);
        if (roleEnd == -1) {
            return content;
        }
        return content.substring(0, roleStart) + role + content.substring(roleEnd);
    }

    public static void setLastChatMessage(String clanName, String role, String nick, String message) {
        lastChatMessages.put(clanName.toLowerCase(), new String[]{role, nick, message});
        String chat = "{\n  \"lastMessage\": {\n    \"role\": \"" + role + "\",\n    \"nick\": \"" + nick + "\",\n    \"message\": \"" + message.replace("\"", "\\\"") + "\"\n  }\n}";
        writeStorageFile(clanName, "chat.db", chat);
    }

    public static String[] getLastChatMessage(String clanName) {
        String[] cached = lastChatMessages.get(clanName.toLowerCase());
        if (cached != null) {
            return cached;
        }
        String content = readStorageFile(clanName, "chat.db");
        if (content == null) {
            return null;
        }
        String role = extractValue(content, "role");
        String nick = extractValue(content, "nick");
        String message = extractValue(content, "message");
        if (role.isEmpty() || nick.isEmpty()) {
            return null;
        }
        String[] result = new String[]{role, nick, message};
        lastChatMessages.put(clanName.toLowerCase(), result);
        return result;
    }

    public static void sendClanMessage(String clanName, String senderName, String senderRole, String message) {
        List<String> members = getClanMembers(clanName);
        String formattedRole = senderRole.substring(0, 1).toUpperCase() + senderRole.substring(1);
        String formattedMessage = Lang.get("gui_chat_format", "role", formattedRole, "player", senderName, "message", message);
        for (String memberName : members) {
            Player member = Server.getInstance().getPlayerExact(memberName);
            if (member != null) {
                member.sendMessage(formattedMessage);
            }
        }
        setLastChatMessage(clanName, formattedRole, senderName, message);
    }

    public static boolean deleteClan(String clanName) {
        boolean deleted = deleteClanStorage(clanName);
        if (deleted) {
            lastChatMessages.remove(clanName.toLowerCase());
        }
        return deleted;
    }

    public static List<String> getClanMembers(String clanName) {
        List<String> members = new ArrayList<>();
        String content = readStorageFile(clanName, "players.db");
        if (content == null) {
            return members;
        }
        int index = 0;
        while ((index = content.indexOf("\"nick\": \"", index)) != -1) {
            index += 9;
            int endIndex = content.indexOf("\"", index);
            if (endIndex != -1) {
                members.add(content.substring(index, endIndex));
            }
        }
        return members;
    }

    public static String getClanTag(String clanName) {
        String content = readStorageFile(clanName, "infoclan.db");
        return content == null ? null : extractValue(content, "tag");
    }

    public static void addPendingInvite(String playerName, String clanName, String leaderName, String clanTag) {
        pendingInvites.put(playerName.toLowerCase(), new String[]{clanName, leaderName, clanTag});
    }

    public static String[] getPendingInvite(String playerName) {
        return pendingInvites.get(playerName.toLowerCase());
    }

    public static void removePendingInvite(String playerName) {
        pendingInvites.remove(playerName.toLowerCase());
    }

    public static boolean hasPendingInvite(String playerName) {
        return pendingInvites.containsKey(playerName.toLowerCase());
    }

    public static boolean addMemberToClan(String clanName, String playerName) {
        String content = readStorageFile(clanName, "players.db");
        if (content == null) {
            return false;
        }
        if (content.contains("\"nick\": \"" + playerName + "\"")) {
            return false;
        }
        int count = extractIntValue(content, "count");
        int maxPlayers = extractIntValue(content, "maxPlayers");
        if (count >= maxPlayers) {
            return false;
        }

        String pid = generateId(32);
        String joinedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String newMember = ",\n    {\n      \"nick\": \"" + playerName + "\",\n      \"pid\": \"" + pid + "\",\n      \"role\": \"member\",\n      \"joinedAt\": \"" + joinedAt + "\"\n    }";

        int arrayCloseIndex = content.lastIndexOf("]");
        content = content.substring(0, arrayCloseIndex - 1) + newMember + "\n  ]\n}";
        content = content.replace("\"count\": " + count, "\"count\": " + (count + 1));
        return writeStorageFile(clanName, "players.db", content);
    }

    public static boolean kickMemberFromClan(String clanName, String playerName) {
        String content = readStorageFile(clanName, "players.db");
        if (content == null || !content.contains("\"nick\": \"" + playerName + "\"")) {
            return false;
        }
        int memberStart = content.indexOf("\"nick\": \"" + playerName + "\"");
        int blockStart = content.lastIndexOf("{", memberStart);
        int blockEnd = content.indexOf("}", memberStart) + 1;
        String before = content.substring(0, blockStart).trim();
        String after = content.substring(blockEnd).trim();
        String newContent;
        if (before.endsWith(",")) {
            newContent = content.substring(0, before.length() - 1) + "\n    " + content.substring(blockEnd);
        } else if (after.startsWith(",")) {
            newContent = content.substring(0, blockStart) + content.substring(blockEnd + 1);
        } else {
            newContent = content.substring(0, blockStart) + content.substring(blockEnd);
        }
        int count = extractIntValue(content, "count");
        newContent = newContent.replace("\"count\": " + count, "\"count\": " + (count - 1));
        newContent = newContent.replaceAll("\\n\\s*\\n\\s*\\n", "\n\n");
        return writeStorageFile(clanName, "players.db", newContent);
    }

    public static boolean updateClanName(String oldName, String newName) {
        if (!clanExists(oldName) || clanExists(newName)) {
            return false;
        }

        List<String> files = new ArrayList<>();
        files.add("id.json");
        files.add("infoclan.db");
        files.add("players.db");
        files.add("chat.db");
        files.add("homes.db");

        Map<String, String> contentMap = new HashMap<>();
        for (String file : files) {
            String content = readStorageFile(oldName, file);
            if (content != null) {
                if ("infoclan.db".equals(file)) {
                    content = content.replace("\"name\": \"" + oldName + "\"", "\"name\": \"" + newName + "\"");
                }
                contentMap.put(file, content);
            }
        }

        for (Map.Entry<String, String> entry : contentMap.entrySet()) {
            if (!writeStorageFile(newName, entry.getKey(), entry.getValue())) {
                return false;
            }
        }

        deleteClanStorage(oldName);
        return true;
    }

    public static boolean updateClanDescription(String clanName, String description) {
        String content = readStorageFile(clanName, "infoclan.db");
        if (content == null) {
            return false;
        }
        if (content.contains("\"description\":")) {
            content = content.replaceAll("\"description\": \"[^\"]*\"", "\"description\": \"" + description + "\"");
        } else {
            content = content.replace("\n}", ",\n  \"description\": \"" + description + "\"\n}");
        }
        return writeStorageFile(clanName, "infoclan.db", content);
    }

    public static boolean createClan(Player creator, String clanName, String clanTag) {
        if (clanExists(clanName)) {
            return false;
        }
        String clanId = generateId(64);
        String creatorPid = generateId(32);
        String creatorIp = creator.getAddress();
        String creatorNick = creator.getName();
        String createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String idJson = "{\n  \"clanId\": \"" + clanId + "\",\n  \"creator\": {\n    \"ip\": \"" + creatorIp + "\",\n    \"nick\": \"" + creatorNick + "\",\n    \"pid\": \"" + creatorPid + "\"\n  }\n}";
        String infoClan = "{\n  \"name\": \"" + clanName + "\",\n  \"tag\": \"" + clanTag + "\",\n  \"leader\": \"" + creatorNick + "\",\n  \"createdAt\": \"" + createdDate + "\",\n  \"treasury\": 0,\n  \"level\": 1,\n  \"rank\": \"None\",\n  \"title\": \"None\",\n  \"regions\": 0,\n  \"homePoints\": \"None\",\n  \"wins\": 0,\n  \"losses\": 0,\n  \"deaths\": 0\n}";
        String playersDb = "{\n  \"maxPlayers\": " + MAX_PLAYERS + ",\n  \"count\": 1,\n  \"members\": [\n    {\n      \"nick\": \"" + creatorNick + "\",\n      \"pid\": \"" + creatorPid + "\",\n      \"role\": \"leader\",\n      \"joinedAt\": \"" + createdDate + "\"\n    }\n  ]\n}";

        boolean ok = writeStorageFile(clanName, "id.json", idJson)
                && writeStorageFile(clanName, "infoclan.db", infoClan)
                && writeStorageFile(clanName, "players.db", playersDb);

        if (ok) {
            Server.getInstance().getLogger().info("[ClanGuard] Clan \"" + clanName + "\" created with ID: " + clanId);
        }
        return ok;
    }

    private static String generateId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ID_CHARS.charAt(RANDOM.nextInt(ID_CHARS.length())));
        }
        return sb.toString();
    }

    private static String extractValue(String content, String key) {
        String search = "\"" + key + "\": \"";
        int start = content.indexOf(search);
        if (start == -1) {
            return "";
        }
        start += search.length();
        int end = content.indexOf("\"", start);
        return end == -1 ? "" : content.substring(start, end);
    }

    private static String extractValueOrDefault(String content, String key, String defaultValue) {
        String value = extractValue(content, key);
        return value.isEmpty() ? defaultValue : value;
    }

    private static int extractIntValue(String content, String key) {
        String search = "\"" + key + "\": ";
        int start = content.indexOf(search);
        if (start == -1) {
            return 0;
        }
        start += search.length();
        int end = start;
        while (end < content.length() && (Character.isDigit(content.charAt(end)) || content.charAt(end) == '-')) {
            end++;
        }
        try {
            return Integer.parseInt(content.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long extractLongValue(String content, String key) {
        String search = "\"" + key + "\": ";
        int start = content.indexOf(search);
        if (start == -1) {
            return 0L;
        }
        start += search.length();
        int end = start;
        while (end < content.length() && (Character.isDigit(content.charAt(end)) || content.charAt(end) == '-')) {
            end++;
        }
        try {
            return Long.parseLong(content.substring(start, end));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static int extractIntValueOrDefault(String content, String key, int defaultValue) {
        String search = "\"" + key + "\": ";
        if (!content.contains(search)) {
            return defaultValue;
        }
        return extractIntValue(content, key);
    }
}