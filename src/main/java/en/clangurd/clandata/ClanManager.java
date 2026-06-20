package en.clangurd.clandata;

import cn.nukkit.Player;
import cn.nukkit.Server;
import en.clangurd.lang.Lang;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

public final class ClanManager {

    private static final String PLUGIN_FOLDER = "plugins/clanguard";
    private static final int MAX_PLAYERS = 150;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Map<String, String[]> pendingInvites = new HashMap<>();
    private static final Map<String, String[]> lastChatMessages = new HashMap<>();

    private ClanManager() {
    }

    public static boolean clanExists(String clanName) {
        File clanFolder = new File(PLUGIN_FOLDER, clanName);
        return clanFolder.exists();
    }

    public static boolean tagExists(String tag) {
        File pluginFolder = new File(PLUGIN_FOLDER);
        if (!pluginFolder.exists()) return false;
        File[] clanFolders = pluginFolder.listFiles(File::isDirectory);
        if (clanFolders == null) return false;
        for (File folder : clanFolders) {
            File infoFile = new File(folder, "infoclan.db");
            if (infoFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(infoFile.toPath()));
                    if (content.contains("\"tag\":\"" + tag + "\"")) return true;
                } catch (IOException ignored) {}
            }
        }
        return false;
    }

    public static String getPlayerClan(String playerName) {
        File pluginFolder = new File(PLUGIN_FOLDER);
        if (!pluginFolder.exists()) return null;
        File[] clanFolders = pluginFolder.listFiles(File::isDirectory);
        if (clanFolders == null) return null;
        for (File folder : clanFolders) {
            File playersFile = new File(folder, "players.db");
            if (playersFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(playersFile.toPath()));
                    if (content.contains("\"nick\": \"" + playerName + "\"")) return folder.getName();
                } catch (IOException ignored) {}
            }
        }
        return null;
    }

    public static boolean isPlayerLeader(String playerName, String clanName) {
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        if (!infoFile.exists()) return false;
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            return content.contains("\"leader\": \"" + playerName + "\"");
        } catch (IOException e) {
            return false;
        }
    }

    public static String getPlayerRole(String playerName, String clanName) {
        File playersFile = new File(PLUGIN_FOLDER + "/" + clanName, "players.db");
        if (!playersFile.exists()) return "member";
        try {
            String content = new String(Files.readAllBytes(playersFile.toPath()));
            int nickIndex = content.indexOf("\"nick\": \"" + playerName + "\"");
            if (nickIndex == -1) return "member";
            int roleIndex = content.indexOf("\"role\": \"", nickIndex);
            if (roleIndex == -1) return "member";
            roleIndex += 9;
            int roleEnd = content.indexOf("\"", roleIndex);
            return content.substring(roleIndex, roleEnd);
        } catch (IOException e) {
            return "member";
        }
    }

    public static Map<String, Object> getClanInfo(String clanName) {
        Map<String, Object> info = new HashMap<>();
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        File playersFile = new File(PLUGIN_FOLDER + "/" + clanName, "players.db");
        if (!infoFile.exists() || !playersFile.exists()) return null;
        try {
            String infoContent = new String(Files.readAllBytes(infoFile.toPath()));
            String playersContent = new String(Files.readAllBytes(playersFile.toPath()));
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
            info.put("homePoints", extractValueOrDefault(infoContent, "homePoints", "None"));
            info.put("wins", extractIntValueOrDefault(infoContent, "wins", 0));
            info.put("losses", extractIntValueOrDefault(infoContent, "losses", 0));
            info.put("deaths", extractIntValueOrDefault(infoContent, "deaths", 0));
            info.put("maxPlayers", extractIntValue(playersContent, "maxPlayers"));
            info.put("count", extractIntValue(playersContent, "count"));
            return info;
        } catch (IOException e) {
            return null;
        }
    }

    public static long getClanTreasury(String clanName) {
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        if (!infoFile.exists()) return 0;
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            return extractLongValue(content, "treasury");
        } catch (IOException e) {
            return 0;
        }
    }

    public static double calculateCommission(long amount) {
        double percentage = 1.0 + (Math.log10(amount / 10000.0) * 3.0);
        percentage = Math.max(1.0, Math.min(10.0, percentage));
        return percentage / 100.0;
    }

    public static boolean depositToTreasury(String clanName, String playerName, long amount) {
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        if (!infoFile.exists()) return false;
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            long currentTreasury = extractLongValue(content, "treasury");
            double commission = calculateCommission(amount);
            long commissionAmount = (long) (amount * commission);
            long depositAmount = amount - commissionAmount;
            long newTreasury = currentTreasury + depositAmount;
            content = content.replaceAll("\"treasury\": \\d+", "\"treasury\": " + newTreasury);
            writeFile(infoFile, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean withdrawFromTreasury(String clanName, String playerName, long amount) {
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        if (!infoFile.exists()) return false;
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            long currentTreasury = extractLongValue(content, "treasury");
            if (amount > currentTreasury) return false;
            long newTreasury = currentTreasury - amount;
            content = content.replaceAll("\"treasury\": \\d+", "\"treasury\": " + newTreasury);
            writeFile(infoFile, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void setLastChatMessage(String clanName, String role, String nick, String message) {
        lastChatMessages.put(clanName.toLowerCase(), new String[]{role, nick, message});
        saveChatMessage(clanName, role, nick, message);
    }

    public static String[] getLastChatMessage(String clanName) {
        String[] cached = lastChatMessages.get(clanName.toLowerCase());
        if (cached != null) return cached;
        return loadLastChatMessage(clanName);
    }

    private static void saveChatMessage(String clanName, String role, String nick, String message) {
        File chatFile = new File(PLUGIN_FOLDER + "/" + clanName, "chat.db");
        try {
            String content = "{\n  \"lastMessage\": {\n    \"role\": \"" + role + "\",\n    \"nick\": \"" + nick + "\",\n    \"message\": \"" + message.replace("\"", "\\\"") + "\"\n  }\n}";
            writeFile(chatFile, content);
        } catch (IOException ignored) {}
    }

    private static String[] loadLastChatMessage(String clanName) {
        File chatFile = new File(PLUGIN_FOLDER + "/" + clanName, "chat.db");
        if (!chatFile.exists()) return null;
        try {
            String content = new String(Files.readAllBytes(chatFile.toPath()));
            String role = extractValue(content, "role");
            String nick = extractValue(content, "nick");
            String message = extractValue(content, "message");
            if (!role.isEmpty() && !nick.isEmpty()) {
                String[] result = new String[]{role, nick, message};
                lastChatMessages.put(clanName.toLowerCase(), result);
                return result;
            }
        } catch (IOException ignored) {}
        return null;
    }

    public static void sendClanMessage(String clanName, String senderName, String senderRole, String message) {
        List<String> members = getClanMembers(clanName);
        String formattedRole = senderRole.substring(0, 1).toUpperCase() + senderRole.substring(1);
        String formattedMessage = Lang.get("gui_chat_format", "role", formattedRole, "player", senderName, "message", message);
        for (String memberName : members) {
            Player member = Server.getInstance().getPlayerExact(memberName);
            if (member != null) member.sendMessage(formattedMessage);
        }
        setLastChatMessage(clanName, formattedRole, senderName, message);
    }

    public static boolean deleteClan(String clanName) {
        File clanFolder = new File(PLUGIN_FOLDER, clanName);
        if (!clanFolder.exists()) return false;
        try {
            File[] files = clanFolder.listFiles();
            if (files != null) {
                for (File file : files) file.delete();
            }
            boolean deleted = clanFolder.delete();
            if (deleted) lastChatMessages.remove(clanName.toLowerCase());
            return deleted;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<String> getClanMembers(String clanName) {
        List<String> members = new ArrayList<>();
        File playersFile = new File(PLUGIN_FOLDER + "/" + clanName, "players.db");
        if (!playersFile.exists()) return members;
        try {
            String content = new String(Files.readAllBytes(playersFile.toPath()));
            int index = 0;
            while ((index = content.indexOf("\"nick\": \"", index)) != -1) {
                index += 9;
                int endIndex = content.indexOf("\"", index);
                if (endIndex != -1) members.add(content.substring(index, endIndex));
            }
        } catch (IOException ignored) {}
        return members;
    }

    public static String getClanTag(String clanName) {
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        if (!infoFile.exists()) return null;
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            return extractValue(content, "tag");
        } catch (IOException e) {
            return null;
        }
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
        File playersFile = new File(PLUGIN_FOLDER + "/" + clanName, "players.db");
        if (!playersFile.exists()) return false;
        try {
            String content = new String(Files.readAllBytes(playersFile.toPath()));
            if (content.contains("\"nick\": \"" + playerName + "\"")) return false;
            int count = extractIntValue(content, "count");
            int maxPlayers = extractIntValue(content, "maxPlayers");
            if (count >= maxPlayers) return false;
            String pid = generateId(32);
            String joinedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String newMember = ",\n    {\n      \"nick\": \"" + playerName + "\",\n      \"pid\": \"" + pid + "\",\n      \"role\": \"member\",\n      \"joinedAt\": \"" + joinedAt + "\"\n    }";
            int arrayCloseIndex = content.lastIndexOf("]");
            content = content.substring(0, arrayCloseIndex - 1) + newMember + "\n  ]\n}";
            content = content.replace("\"count\": " + count, "\"count\": " + (count + 1));
            writeFile(playersFile, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean kickMemberFromClan(String clanName, String playerName) {
        File playersFile = new File(PLUGIN_FOLDER + "/" + clanName, "players.db");
        if (!playersFile.exists()) return false;
        try {
            String content = new String(Files.readAllBytes(playersFile.toPath()));
            if (!content.contains("\"nick\": \"" + playerName + "\"")) return false;
            int memberStart = content.indexOf("\"nick\": \"" + playerName + "\"");
            if (memberStart == -1) return false;
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
            writeFile(playersFile, newContent);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean updateClanName(String oldName, String newName) {
        File oldFolder = new File(PLUGIN_FOLDER, oldName);
        File newFolder = new File(PLUGIN_FOLDER, newName);
        if (!oldFolder.exists() || newFolder.exists()) return false;
        File infoFile = new File(oldFolder, "infoclan.db");
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            content = content.replace("\"name\": \"" + oldName + "\"", "\"name\": \"" + newName + "\"");
            writeFile(infoFile, content);
        } catch (IOException e) {
            return false;
        }
        return oldFolder.renameTo(newFolder);
    }

    public static boolean updateClanDescription(String clanName, String description) {
        File infoFile = new File(PLUGIN_FOLDER + "/" + clanName, "infoclan.db");
        if (!infoFile.exists()) return false;
        try {
            String content = new String(Files.readAllBytes(infoFile.toPath()));
            if (content.contains("\"description\":")) {
                content = content.replaceAll("\"description\": \"[^\"]*\"", "\"description\": \"" + description + "\"");
            } else {
                content = content.replace("\n}", ",\n  \"description\": \"" + description + "\"\n}");
            }
            writeFile(infoFile, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean createClan(Player creator, String clanName, String clanTag) {
        File pluginFolder = new File(PLUGIN_FOLDER);
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        File clanFolder = new File(pluginFolder, clanName);
        if (clanFolder.exists()) return false;
        clanFolder.mkdirs();
        String clanId = generateId(64);
        String creatorPid = generateId(32);
        String creatorIp = creator.getAddress();
        String creatorNick = creator.getName();
        String createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String idJson = "{\n  \"clanId\": \"" + clanId + "\",\n  \"creator\": {\n    \"ip\": \"" + creatorIp + "\",\n    \"nick\": \"" + creatorNick + "\",\n    \"pid\": \"" + creatorPid + "\"\n  }\n}";
        String infoClan = "{\n  \"name\": \"" + clanName + "\",\n  \"tag\": \"" + clanTag + "\",\n  \"leader\": \"" + creatorNick + "\",\n  \"createdAt\": \"" + createdDate + "\",\n  \"treasury\": 0,\n  \"level\": 1,\n  \"rank\": \"None\",\n  \"title\": \"None\",\n  \"regions\": 0,\n  \"homePoints\": \"None\",\n  \"wins\": 0,\n  \"losses\": 0,\n  \"deaths\": 0\n}";
        String playersDb = "{\n  \"maxPlayers\": " + MAX_PLAYERS + ",\n  \"count\": 1,\n  \"members\": [\n    {\n      \"nick\": \"" + creatorNick + "\",\n      \"pid\": \"" + creatorPid + "\",\n      \"role\": \"leader\",\n      \"joinedAt\": \"" + createdDate + "\"\n    }\n  ]\n}";
        try {
            writeFile(new File(clanFolder, "id.json"), idJson);
            writeFile(new File(clanFolder, "infoclan.db"), infoClan);
            writeFile(new File(clanFolder, "players.db"), playersDb);
            Server.getInstance().getLogger().info("[ClanGuard] Clan \"" + clanName + "\" created with ID: " + clanId);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String generateId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(ID_CHARS.charAt(RANDOM.nextInt(ID_CHARS.length())));
        return sb.toString();
    }

    private static void writeFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    private static String extractValue(String content, String key) {
        String search = "\"" + key + "\": \"";
        int start = content.indexOf(search);
        if (start == -1) return "";
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
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < content.length() && (Character.isDigit(content.charAt(end)) || content.charAt(end) == '-')) end++;
        try {
            return Integer.parseInt(content.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long extractLongValue(String content, String key) {
        String search = "\"" + key + "\": ";
        int start = content.indexOf(search);
        if (start == -1) return 0L;
        start += search.length();
        int end = start;
        while (end < content.length() && (Character.isDigit(content.charAt(end)) || content.charAt(end) == '-')) end++;
        try {
            return Long.parseLong(content.substring(start, end));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static int extractIntValueOrDefault(String content, String key, int defaultValue) {
        String search = "\"" + key + "\": ";
        if (!content.contains(search)) return defaultValue;
        return extractIntValue(content, key);
    }
}
