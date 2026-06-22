package en.clangurd.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.DummyBossBar;
import en.clangurd.ClanGuard;
import en.clangurd.clandata.ClanManager;
import en.clangurd.clandata.ClanManager.HomePoint;
import en.clangurd.gui.*;
import en.clangurd.lang.Lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class FormListener implements Listener {

    private static final Pattern TAG_PATTERN = Pattern.compile("^[A-Z][A-Za-z0-9]{2,5}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern HOME_NAME_PATTERN = Pattern.compile("^[A-Z][A-Za-z0-9]{0,5}$");
    private static final long[] DEPOSIT_AMOUNTS = {10000, 50000, 100000, 500000, 1000000, 5000000, 10000000};

    private final ClanGuard plugin;
    private final Map<String, String> pendingKicks = new HashMap<>();
    private final Map<String, String> pendingHomeTeleport = new HashMap<>();
    private final Map<String, String> pendingOwnershipTransfer = new HashMap<>();
    private final Map<String, Integer> teleportTaskIds = new HashMap<>();
    private final Map<String, Long> teleportBossBars = new HashMap<>();
    private final Map<String, Vector3> teleportStart = new HashMap<>();

    public FormListener(ClanGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        int formId = event.getFormID();
        FormWindow window = event.getWindow();
        if (window.getResponse() == null) {
            return;
        }

        switch (formId) {
            case 1000: handleMainMenu(player, (FormWindowSimple) window); break;
            case 1001: handleCreateClan(player, (FormWindowCustom) window); break;
            case 1002: handleChangeName(player, (FormWindowCustom) window); break;
            case 1003: handleDescription(player, (FormWindowCustom) window); break;
            case 1005: handleInvite(player, (FormWindowCustom) window); break;
            case 1006: handleInviteResponse(player, (FormWindowSimple) window); break;
            case 1007: handleKickSelect(player, (FormWindowSimple) window); break;
            case 1008: handleKickConfirm(player, (FormWindowSimple) window); break;
            case 1010: handleTreasuryDeposit(player, (FormWindowSimple) window); break;
            case 1011: handleCustomDeposit(player, (FormWindowCustom) window); break;
            case 1012: handleTreasuryWithdraw(player, (FormWindowSimple) window); break;
            case 1013: handleCustomWithdraw(player, (FormWindowCustom) window); break;
            case 1014: handleClanChat(player, (FormWindowCustom) window); break;
            case 1015: handleDeleteClan(player, (FormWindowSimple) window); break;
            case 1016: handleSetHomePoint(player, (FormWindowCustom) window); break;
            case 1017: handleSelectHomePoint(player, (FormWindowSimple) window); break;
            case 1018: handleTeleportHomeConfirm(player, (FormWindowSimple) window); break;
            case 1019: handleSelectTransferTarget(player, (FormWindowSimple) window); break;
            case 1020: handleTransferOwnershipConfirm(player, (FormWindowSimple) window); break;
            default: break;
        }
    }

    private void handleMainMenu(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }
        switch (response.getClickedButtonId()) {
            case 0: ClanCreateGui.open(player); break;
            case 1: handleClanInfoButton(player); break;
            case 2: handleInviteButton(player); break;
            case 3: handleKickButton(player); break;
            case 4: handleDepositButton(player); break;
            case 5: handleWithdrawButton(player); break;
            case 6: handleClanChatButton(player); break;
            case 7: handleSetHomeButton(player); break;
            case 8: handleTeleportHomeButton(player); break;
            case 9: handleTransferOwnershipButton(player); break;
            case 10: handleChangeNameButton(player); break;
            case 11: handleDescriptionButton(player); break;
            case 12: handleDeleteClanButton(player); break;
            default: break;
        }
    }

    private void handleClanInfoButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        ClanInfoGui.open(player, clanName);
    }

    private void handleInviteButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        ClanInviteGui.open(player);
    }

    private void handleKickButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        ClanKickGui.open(player, clanName);
    }

    private void handleDepositButton(Player player) {
        if (!ClanGuard.isEconomyEnabled()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_economy_disabled"));
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        ClanTreasuryDepositGui.open(player, ClanManager.getClanTreasury(clanName));
    }

    private void handleWithdrawButton(Player player) {
        if (!ClanGuard.isEconomyEnabled()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_economy_disabled"));
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        long treasury = ClanManager.getClanTreasury(clanName);
        if (treasury <= 0) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_treasury_empty"));
            return;
        }
        ClanTreasuryWithdrawGui.open(player, treasury);
    }

    private void handleClanChatButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        String[] lastMsg = ClanManager.getLastChatMessage(clanName);
        ClanChatGui.open(player, lastMsg != null ? lastMsg[0] : "", lastMsg != null ? lastMsg[1] : "", lastMsg != null ? lastMsg[2] : "");
    }

    private void handleSetHomeButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        ClanSetHomeGui.open(player);
    }

    private void handleTeleportHomeButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        List<String> points = ClanManager.getHomePointNames(clanName);
        if (points.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_no_home_points"));
            return;
        }
        ClanTeleportHomeListGui.open(player, points);
    }

    private void handleTransferOwnershipButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        List<String> members = ClanManager.getClanMembers(clanName);
        members.removeIf(name -> name.equalsIgnoreCase(player.getName()));
        if (members.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_transfer_target_invalid"));
            return;
        }
        ClanTransferOwnershipGui.open(player, members);
    }

    private void handleChangeNameButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        ClanChangeNameGui.open(player);
    }

    private void handleDescriptionButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        ClanDescriptionGui.open(player);
    }

    private void handleDeleteClanButton(Player player) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        ClanDeleteGui.open(player, clanName, ClanManager.getClanTag(clanName));
    }

    private void handleSetHomePoint(Player player, FormWindowCustom window) {
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        String homeName = response.getInputResponse(1).trim();
        if (!HOME_NAME_PATTERN.matcher(homeName).matches()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_home_name_invalid"));
            return;
        }

        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null || !ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }

        boolean created = ClanManager.createHomePoint(clanName, homeName, player);
        if (!created) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_home_exists"));
            return;
        }

        String tag = ClanManager.getClanTag(clanName);
        player.sendMessage(Lang.get("prefix") + Lang.get("success_home_created", "home", homeName, "tag", tag));
        for (String memberName : ClanManager.getClanMembers(clanName)) {
            Player member = Server.getInstance().getPlayerExact(memberName);
            if (member != null && !member.getName().equalsIgnoreCase(player.getName())) {
                member.sendMessage(Lang.get("prefix_clans") + Lang.get("broadcast_home_created", "home", homeName, "tag", tag));
            }
        }
    }

    private void handleSelectHomePoint(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        List<String> points = ClanManager.getHomePointNames(clanName);
        int id = response.getClickedButtonId();
        if (id < 0 || id >= points.size()) {
            return;
        }
        String point = points.get(id);
        pendingHomeTeleport.put(player.getName(), point);
        ClanTeleportHomeConfirmGui.open(player, point);
    }

    private void handleTeleportHomeConfirm(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            pendingHomeTeleport.remove(player.getName());
            return;
        }
        if (response.getClickedButtonId() != 0) {
            pendingHomeTeleport.remove(player.getName());
            return;
        }

        String homeName = pendingHomeTeleport.remove(player.getName());
        if (homeName == null) {
            return;
        }
        startTeleportCountdown(player, homeName);
    }

    private void startTeleportCountdown(final Player player, final String homeName) {
        final String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }

        final HomePoint point = ClanManager.getHomePoint(clanName, homeName);
        if (point == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_home_not_found"));
            return;
        }

        cancelTeleport(player.getName(), null);

        final DummyBossBar bossBar = new DummyBossBar.Builder(player)
                .text(Lang.get("title_teleport_bar", "seconds", "10"))
                .length(100)
                .build();
        final long bossBarId = player.createBossBar(bossBar);

        teleportBossBars.put(player.getName(), bossBarId);
        teleportStart.put(player.getName(), new Vector3(player.getX(), player.getY(), player.getZ()));
        player.sendMessage(Lang.get("prefix") + Lang.get("notify_teleport_started"));

        Task countdown = new Task() {
            private int seconds = 10;

            @Override
            public void onRun(int currentTick) {
                Player online = Server.getInstance().getPlayerExact(player.getName());
                if (online == null || !online.isOnline()) {
                    cancelTeleport(player.getName(), null);
                    Server.getInstance().getScheduler().cancelTask(this.getTaskId());
                    return;
                }

                Vector3 start = teleportStart.get(player.getName());
                if (start != null && online.distanceSquared(start) > 0.01) {
                    cancelTeleport(player.getName(), Lang.get("prefix") + Lang.get("error_teleport_cancelled_moved"));
                    Server.getInstance().getScheduler().cancelTask(this.getTaskId());
                    return;
                }

                if (seconds <= 0) {
                    Level level = Server.getInstance().getLevelByName(point.getLevel());
                    if (level == null) {
                        level = online.getLevel();
                    }
                    online.teleport(new Position(point.getX(), point.getY(), point.getZ(), level));
                    cancelTeleport(player.getName(), null);
                    Server.getInstance().getScheduler().cancelTask(this.getTaskId());
                    return;
                }

                online.sendTitle(Lang.get("title_teleport_main"), Lang.get("title_teleport_subtitle"), 0, 20, 0);
                bossBar.setText(Lang.get("title_teleport_bar", "seconds", String.valueOf(seconds)));
                bossBar.setLength(seconds * 10);
                seconds--;
            }
        };

        int taskId = Server.getInstance().getScheduler().scheduleRepeatingTask(plugin, countdown, 20).getTaskId();
        teleportTaskIds.put(player.getName(), taskId);
    }

    private void cancelTeleport(String playerName, String message) {
        Integer taskId = teleportTaskIds.remove(playerName);
        if (taskId != null) {
            Server.getInstance().getScheduler().cancelTask(taskId);
        }

        Player player = Server.getInstance().getPlayerExact(playerName);
        Long bossBarId = teleportBossBars.remove(playerName);
        if (player != null && bossBarId != null) {
            player.removeBossBar(bossBarId);
            player.sendTitle("", "", 0, 1, 0);
        }

        teleportStart.remove(playerName);

        if (player != null && message != null && !message.isEmpty()) {
            player.sendMessage(message);
        }
    }

    private void handleSelectTransferTarget(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }

        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            return;
        }

        List<String> members = ClanManager.getClanMembers(clanName);
        members.removeIf(name -> name.equalsIgnoreCase(player.getName()));
        int id = response.getClickedButtonId();
        if (id < 0 || id >= members.size()) {
            return;
        }

        String target = members.get(id);
        pendingOwnershipTransfer.put(player.getName(), target);
        ClanTransferOwnershipConfirmGui.open(player, target);
    }

    private void handleTransferOwnershipConfirm(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            pendingOwnershipTransfer.remove(player.getName());
            return;
        }
        if (response.getClickedButtonId() != 0) {
            pendingOwnershipTransfer.remove(player.getName());
            return;
        }

        String target = pendingOwnershipTransfer.remove(player.getName());
        if (target == null) {
            return;
        }

        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }

        boolean success = ClanManager.transferOwnership(clanName, player.getName(), target);
        if (!success) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
            return;
        }

        String tag = ClanManager.getClanTag(clanName);
        player.sendMessage(Lang.get("prefix") + Lang.get("success_transfer_ownership", "player", target));

        Player targetPlayer = Server.getInstance().getPlayerExact(target);
        if (targetPlayer != null) {
            targetPlayer.sendMessage(Lang.get("prefix") + Lang.get("notify_you_are_new_leader", "tag", tag));
        }

        for (String memberName : ClanManager.getClanMembers(clanName)) {
            Player member = Server.getInstance().getPlayerExact(memberName);
            if (member != null && !member.getName().equalsIgnoreCase(player.getName()) && !member.getName().equalsIgnoreCase(target)) {
                member.sendMessage(Lang.get("prefix_clans") + Lang.get("broadcast_ownership_transferred", "from", player.getName(), "to", target));
            }
        }
    }

    private void handleCreateClan(Player player, FormWindowCustom window) {
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        String clanName = response.getInputResponse(1).trim();
        String clanTag = response.getInputResponse(2).trim();
        if (clanName.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_empty_name"));
            return;
        }
        if (!NAME_PATTERN.matcher(clanName).matches()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_invalid_name"));
            return;
        }
        if (clanTag.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_empty_tag"));
            return;
        }
        if (!TAG_PATTERN.matcher(clanTag).matches()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_invalid_tag"));
            return;
        }
        String existingClan = ClanManager.getPlayerClan(player.getName());
        if (existingClan != null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_already_in_clan") + existingClan);
            return;
        }
        if (ClanManager.clanExists(clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_clan_exists"));
            return;
        }
        if (ClanManager.tagExists(clanTag)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_tag_exists"));
            return;
        }
        if (ClanManager.createClan(player, clanName, clanTag)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("success_clan_created", "name", clanName, "tag", clanTag));
            Server.getInstance().broadcastMessage(Lang.get("prefix_clans") + Lang.get("broadcast_clan_created", "player", player.getName(), "tag", clanTag));
        } else {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
        }
    }

    private void handleInvite(Player player, FormWindowCustom window) {
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        String targetName = response.getInputResponse(1).trim();
        if (targetName.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_player_offline"));
            return;
        }
        Player target = Server.getInstance().getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_player_offline"));
            return;
        }
        if (target.getName().equalsIgnoreCase(player.getName())) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_cannot_invite_self"));
            return;
        }
        if (ClanManager.getPlayerClan(target.getName()) != null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_player_in_clan"));
            return;
        }
        if (ClanManager.hasPendingInvite(target.getName())) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_invite_pending"));
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        String clanTag = ClanManager.getClanTag(clanName);
        ClanManager.addPendingInvite(target.getName(), clanName, player.getName(), clanTag);
        player.sendMessage(Lang.get("prefix") + Lang.get("success_invite_sent", "player", target.getName()));
        ClanInviteReceivedGui.open(target, player.getName(), clanName, clanTag);
    }

    private void handleInviteResponse(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            ClanManager.removePendingInvite(player.getName());
            return;
        }
        String[] invite = ClanManager.getPendingInvite(player.getName());
        if (invite == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_invite_expired"));
            return;
        }
        ClanManager.removePendingInvite(player.getName());
        if (response.getClickedButtonId() == 0) {
            if (ClanManager.addMemberToClan(invite[0], player.getName())) {
                player.sendMessage(Lang.get("prefix") + Lang.get("success_joined_clan"));
                for (String memberName : ClanManager.getClanMembers(invite[0])) {
                    Player member = Server.getInstance().getPlayerExact(memberName);
                    if (member != null && !member.getName().equalsIgnoreCase(player.getName())) {
                        member.sendMessage(Lang.get("prefix_clans") + Lang.get("broadcast_member_joined", "player", player.getName()));
                    }
                }
            } else {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_clan_full"));
            }
        } else {
            player.sendMessage(Lang.get("prefix") + Lang.get("success_invite_declined"));
            Player leader = Server.getInstance().getPlayerExact(invite[1]);
            if (leader != null) {
                leader.sendMessage(Lang.get("prefix") + Lang.get("notify_invite_declined", "player", player.getName()));
            }
        }
    }

    private void handleKickSelect(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        List<String> members = ClanManager.getClanMembers(clanName);
        members.removeIf(m -> m.equalsIgnoreCase(player.getName()));
        int buttonId = response.getClickedButtonId();
        if (buttonId >= 0 && buttonId < members.size()) {
            pendingKicks.put(player.getName(), members.get(buttonId));
            ClanKickConfirmGui.open(player, members.get(buttonId));
        }
    }

    private void handleKickConfirm(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            pendingKicks.remove(player.getName());
            return;
        }
        String memberToKick = pendingKicks.remove(player.getName());
        if (memberToKick == null) {
            return;
        }
        if (response.getClickedButtonId() == 0) {
            String clanName = ClanManager.getPlayerClan(player.getName());
            if (ClanManager.kickMemberFromClan(clanName, memberToKick)) {
                player.sendMessage(Lang.get("prefix") + Lang.get("success_player_kicked", "player", memberToKick));
                Player kickedPlayer = Server.getInstance().getPlayerExact(memberToKick);
                if (kickedPlayer != null) {
                    kickedPlayer.sendMessage(Lang.get("prefix") + Lang.get("notify_kicked"));
                }
                for (String member : ClanManager.getClanMembers(clanName)) {
                    Player memberPlayer = Server.getInstance().getPlayerExact(member);
                    if (memberPlayer != null && !memberPlayer.getName().equalsIgnoreCase(player.getName())) {
                        memberPlayer.sendMessage(Lang.get("prefix_clans") + Lang.get("broadcast_member_kicked", "player", memberToKick));
                    }
                }
            } else {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
            }
        } else {
            player.sendMessage(Lang.get("prefix") + Lang.get("success_kick_cancelled"));
        }
    }

    private void handleTreasuryDeposit(Player player, FormWindowSimple window) {
        if (!ClanGuard.isEconomyEnabled()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_economy_disabled"));
            return;
        }
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }
        int buttonId = response.getClickedButtonId();
        if (buttonId == 7) {
            ClanTreasuryCustomDepositGui.open(player);
            return;
        }
        if (buttonId >= 0 && buttonId < DEPOSIT_AMOUNTS.length) {
            processDeposit(player, DEPOSIT_AMOUNTS[buttonId]);
        }
    }

    private void handleCustomDeposit(Player player, FormWindowCustom window) {
        if (!ClanGuard.isEconomyEnabled()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_economy_disabled"));
            return;
        }
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        try {
            long amount = Long.parseLong(response.getInputResponse(1).trim().replaceAll("[,.]", ""));
            if (amount < 1000) {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_amount_min"));
                return;
            }
            if (amount > 10000000) {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_amount_max_deposit"));
                return;
            }
            processDeposit(player, amount);
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_amount_invalid"));
        }
    }

    private void processDeposit(Player player, long amount) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        double commission = ClanManager.calculateCommission(amount);
        long commissionAmount = (long) (amount * commission);
        long depositAmount = amount - commissionAmount;
        if (ClanManager.depositToTreasury(clanName, player.getName(), amount)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("success_deposit", "amount", formatMoney(depositAmount), "commission", formatMoney(commissionAmount)));
            for (String memberName : ClanManager.getClanMembers(clanName)) {
                Player member = Server.getInstance().getPlayerExact(memberName);
                if (member != null && !member.getName().equalsIgnoreCase(player.getName())) {
                    member.sendMessage(Lang.get("prefix_clans") + Lang.get("broadcast_deposit", "player", player.getName(), "amount", formatMoney(depositAmount)));
                }
            }
        } else {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
        }
    }

    private void handleTreasuryWithdraw(Player player, FormWindowSimple window) {
        if (!ClanGuard.isEconomyEnabled()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_economy_disabled"));
            return;
        }
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }
        int buttonId = response.getClickedButtonId();
        if (buttonId == 7) {
            String clanName = ClanManager.getPlayerClan(player.getName());
            ClanTreasuryCustomWithdrawGui.open(player, ClanManager.getClanTreasury(clanName));
            return;
        }
        if (buttonId >= 0 && buttonId < DEPOSIT_AMOUNTS.length) {
            processWithdraw(player, DEPOSIT_AMOUNTS[buttonId]);
        }
    }

    private void handleCustomWithdraw(Player player, FormWindowCustom window) {
        if (!ClanGuard.isEconomyEnabled()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_economy_disabled"));
            return;
        }
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        try {
            long amount = Long.parseLong(response.getInputResponse(1).trim().replaceAll("[,.]", ""));
            if (amount < 1000) {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_amount_min"));
                return;
            }
            processWithdraw(player, amount);
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_amount_invalid"));
        }
    }

    private void processWithdraw(Player player, long amount) {
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        long treasury = ClanManager.getClanTreasury(clanName);
        if (amount > treasury) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_treasury_insufficient") + formatMoney(treasury));
            return;
        }
        if (ClanManager.withdrawFromTreasury(clanName, player.getName(), amount)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("success_withdraw", "amount", formatMoney(amount)));
            for (String memberName : ClanManager.getClanMembers(clanName)) {
                Player member = Server.getInstance().getPlayerExact(memberName);
                if (member != null && !member.getName().equalsIgnoreCase(player.getName())) {
                    member.sendMessage(Lang.get("prefix_clans") + Lang.get("broadcast_withdraw", "player", player.getName(), "amount", formatMoney(amount)));
                }
            }
        } else {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
        }
    }

    private void handleClanChat(Player player, FormWindowCustom window) {
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        String message = response.getInputResponse(1).trim();
        if (message.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_empty_message"));
            return;
        }
        if (message.length() > 256) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_message_long"));
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        ClanManager.sendClanMessage(clanName, player.getName(), ClanManager.getPlayerRole(player.getName(), clanName), message);
    }

    private void handleDeleteClan(Player player, FormWindowSimple window) {
        FormResponseSimple response = window.getResponse();
        if (response == null) {
            return;
        }
        if (response.getClickedButtonId() == 0) {
            String clanName = ClanManager.getPlayerClan(player.getName());
            if (clanName == null) {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
                return;
            }
            if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
                return;
            }
            String clanTag = ClanManager.getClanTag(clanName);
            List<String> members = ClanManager.getClanMembers(clanName);
            if (ClanManager.deleteClan(clanName)) {
                player.sendMessage(Lang.get("prefix") + Lang.get("success_clan_deleted"));
                for (String memberName : members) {
                    Player member = Server.getInstance().getPlayerExact(memberName);
                    if (member != null && !member.getName().equalsIgnoreCase(player.getName())) {
                        member.sendMessage(Lang.get("prefix") + Lang.get("notify_clan_deleted", "tag", clanTag));
                    }
                }
                Server.getInstance().broadcastMessage(Lang.get("prefix_clans") + Lang.get("broadcast_clan_deleted", "tag", clanTag));
            } else {
                player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
            }
        } else {
            player.sendMessage(Lang.get("prefix") + Lang.get("success_delete_cancelled"));
        }
    }

    private void handleChangeName(Player player, FormWindowCustom window) {
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        String newName = response.getInputResponse(1).trim();
        if (newName.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_empty_name"));
            return;
        }
        if (!NAME_PATTERN.matcher(newName).matches()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_invalid_name"));
            return;
        }
        String currentClan = ClanManager.getPlayerClan(player.getName());
        if (currentClan == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), currentClan)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        if (ClanManager.clanExists(newName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_clan_exists"));
            return;
        }
        player.sendMessage(Lang.get("prefix") + Lang.get("notify_processing"));
        final String oldName = currentClan;
        final String playerName = player.getName();
        Server.getInstance().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                }
            }

            @Override
            public void onCompletion(Server server) {
                boolean success = ClanManager.updateClanName(oldName, newName);
                Player onlinePlayer = server.getPlayerExact(playerName);
                if (onlinePlayer != null) {
                    if (success) {
                        onlinePlayer.sendMessage(Lang.get("prefix") + Lang.get("success_clan_renamed", "name", newName));
                    } else {
                        onlinePlayer.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
                    }
                }
            }
        });
    }

    private void handleDescription(Player player, FormWindowCustom window) {
        FormResponseCustom response = window.getResponse();
        if (response == null) {
            return;
        }
        String description = response.getInputResponse(1).trim();
        if (description.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_empty_description"));
            return;
        }
        if (description.length() > 200) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_description_long"));
            return;
        }
        String clanName = ClanManager.getPlayerClan(player.getName());
        if (clanName == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_in_clan"));
            return;
        }
        if (!ClanManager.isPlayerLeader(player.getName(), clanName)) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_not_leader"));
            return;
        }
        player.sendMessage(Lang.get("prefix") + Lang.get("notify_processing"));
        final String clan = clanName;
        final String desc = description;
        final String playerName = player.getName();
        Server.getInstance().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
            @Override
            public void onRun() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }

            @Override
            public void onCompletion(Server server) {
                boolean success = ClanManager.updateClanDescription(clan, desc);
                Player onlinePlayer = server.getPlayerExact(playerName);
                if (onlinePlayer != null) {
                    if (success) {
                        onlinePlayer.sendMessage(Lang.get("prefix") + Lang.get("success_description_added"));
                    } else {
                        onlinePlayer.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
                    }
                }
            }
        });
    }

    private String formatMoney(long amount) {
        return String.format("%,d", amount);
    }
}