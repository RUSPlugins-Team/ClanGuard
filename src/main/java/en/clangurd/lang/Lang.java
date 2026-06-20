package en.clangurd.lang;

import cn.nukkit.Server;
import java.util.HashMap;
import java.util.Map;

public final class Lang {

    private static boolean isRussian = false;
    private static final Map<String, String> EN = new HashMap<>();
    private static final Map<String, String> RU = new HashMap<>();

    static {
        EN.put("prefix", "\u00A7e[ClanGuard] ");
        EN.put("prefix_clans", "\u00A7e[CLANS] ");
        EN.put("error_not_in_clan", "\u00A7cYou are not in a clan!");
        EN.put("error_not_leader", "\u00A7cOnly the clan leader can do this!");
        EN.put("error_already_in_clan", "\u00A7cYou are already in a clan: ");
        EN.put("error_clan_exists", "\u00A7cA clan with this name already exists!");
        EN.put("error_tag_exists", "\u00A7cA clan with this TAG already exists!");
        EN.put("error_player_offline", "\u00A7cPlayer is not online!");
        EN.put("error_player_in_clan", "\u00A7cThis player is already in a clan!");
        EN.put("error_invite_pending", "\u00A7cThis player already has a pending invite!");
        EN.put("error_invite_expired", "\u00A7cInvitation expired!");
        EN.put("error_clan_full", "\u00A7cClan is full!");
        EN.put("error_empty_name", "\u00A7cClan name cannot be empty!");
        EN.put("error_empty_tag", "\u00A7cClan TAG cannot be empty!");
        EN.put("error_empty_message", "\u00A7cMessage cannot be empty!");
        EN.put("error_empty_description", "\u00A7cDescription cannot be empty!");
        EN.put("error_invalid_name", "\u00A7cInvalid clan name! 3-20 characters, letters/digits/underscore only.");
        EN.put("error_invalid_tag", "\u00A7cInvalid TAG! 3-6 characters, starts with uppercase, letters/digits only.");
        EN.put("error_description_long", "\u00A7cDescription too long! Maximum 200 characters.");
        EN.put("error_message_long", "\u00A7cMessage too long! Maximum 256 characters.");
        EN.put("error_amount_invalid", "\u00A7cInvalid amount!");
        EN.put("error_amount_min", "\u00A7cMinimum amount is 1,000 coins!");
        EN.put("error_amount_max_deposit", "\u00A7cMaximum deposit is 10,000,000 coins!");
        EN.put("error_treasury_empty", "\u00A7cTreasury is empty!");
        EN.put("error_treasury_insufficient", "\u00A7cNot enough coins in treasury! Available: ");
        EN.put("error_economy_disabled", "\u00A7cEconomy integration is disabled! EconomyAPI not found.");
        EN.put("error_no_members_kick", "\u00A7cThere are no members to kick!");
        EN.put("error_cannot_invite_self", "\u00A7cYou cannot invite yourself!");
        EN.put("error_failed", "\u00A7cOperation failed. Please try again.");
        EN.put("error_console_only", "This command can only be used in-game.");

        EN.put("success_clan_created", "\u00A7aClan \"\u00A7e%name%\u00A7a\" with TAG [\u00A7b%tag%\u00A7a] has been created!");
        EN.put("success_clan_renamed", "\u00A7aYour clan has been successfully renamed to \"\u00A7e%name%\u00A7a\"!");
        EN.put("success_description_added", "\u00A7aClan description has been successfully added!");
        EN.put("success_joined_clan", "\u00A7aYou have successfully joined the clan!");
        EN.put("success_invite_sent", "\u00A7aInvitation sent to \u00A7e%player%\u00A7a!");
        EN.put("success_invite_declined", "\u00A7eYou declined the invitation.");
        EN.put("success_player_kicked", "\u00A7a%player% \u00A7ahas been kicked from the clan!");
        EN.put("success_kick_cancelled", "\u00A7eKick cancelled.");
        EN.put("success_clan_deleted", "\u00A7cClan has been deleted!");
        EN.put("success_delete_cancelled", "\u00A7aClan deletion cancelled.");
        EN.put("success_deposit", "\u00A7aYou deposited \u00A7e%amount% coins\u00A7a to the treasury! (Commission: \u00A7c%commission%\u00A7a)");
        EN.put("success_withdraw", "\u00A7aYou withdrew \u00A7e%amount% coins\u00A7a from the treasury!");

        EN.put("broadcast_clan_created", "\u00A7aPlayer \u00A7b%player% \u00A7acreated the clan [\u00A7b%tag%\u00A7a], congratulations!");
        EN.put("broadcast_member_joined", "\u00A7aA new member \u00A7b%player% \u00A7ahas joined our clan, congratulations!");
        EN.put("broadcast_member_kicked", "\u00A7c%player% \u00A7ehas been kicked from the clan.");
        EN.put("broadcast_clan_deleted", "\u00A7cClan [\u00A7b%tag%\u00A7c] has been disbanded.");
        EN.put("broadcast_deposit", "\u00A7aMember \u00A7b%player% \u00A7adonated \u00A7e%amount% coins\u00A7a to the clan treasury!");
        EN.put("broadcast_withdraw", "\u00A7cClan member \u00A7b%player%\u00A7c took \u00A7e%amount% coins\u00A7c from the clan treasury.");

        EN.put("notify_kicked", "\u00A7cYou have been kicked from the clan!");
        EN.put("notify_clan_deleted", "\u00A7cYour clan [\u00A7b%tag%\u00A7c] has been deleted by the leader.");
        EN.put("notify_invite_declined", "\u00A7e%player% \u00A7cdeclined your invitation.");
        EN.put("notify_processing", "\u00A7eProcessing... You will be notified when complete.");

        EN.put("gui_main_title", "Clan Panel");
        EN.put("gui_main_content", "IMPORTANT INFORMATION!\n\nCreating a clan is FREE.\nHowever, when depositing funds into the clan treasury,\na commission of 1%% to 10%% will be charged from the deposit amount.");
        EN.put("gui_btn_create", "Create Clan");
        EN.put("gui_btn_info", "Clan Info");
        EN.put("gui_btn_invite", "Invite to Clan");
        EN.put("gui_btn_kick", "Kick from Clan");
        EN.put("gui_btn_deposit", "Deposit to Treasury");
        EN.put("gui_btn_withdraw", "Withdraw from Treasury");
        EN.put("gui_btn_chat", "Clan Chat");
        EN.put("gui_btn_rename", "Change Name");
        EN.put("gui_btn_description", "Add Description");
        EN.put("gui_btn_delete", "Delete Clan");
        EN.put("gui_btn_close", "Close");
        EN.put("gui_btn_join", "Join");
        EN.put("gui_btn_decline", "Decline");
        EN.put("gui_btn_kick_confirm", "Kick");
        EN.put("gui_btn_cancel", "Cancel");
        EN.put("gui_btn_delete_confirm", "\u00A7cDelete Clan");
        EN.put("gui_btn_custom_amount", "\u00A7aCustom Amount");

        EN.put("gui_create_title", "Create Clan");
        EN.put("gui_create_content", "Enter a name and a TAG for your clan.\n\nTAG rules:\n- 3 to 6 characters\n- Letters and digits only\n- No spaces or special characters\n- Must start with an uppercase letter");
        EN.put("gui_create_name", "Clan Name");
        EN.put("gui_create_name_hint", "Enter clan name...");
        EN.put("gui_create_tag", "Clan TAG");
        EN.put("gui_create_tag_hint", "Example: ABC");

        EN.put("gui_info_title", "Clan Info - %name%");
        EN.put("gui_info_leader", "\u00A76Clan Leader: \u00A7e%value%");
        EN.put("gui_info_tag", "\u00A76Tag: \u00A7b[%value%]");
        EN.put("gui_info_description", "\u00A76Description: \u00A7f%value%");
        EN.put("gui_info_members", "\u00A76Members: \u00A7a%count% \u00A77/ \u00A7c%max%");
        EN.put("gui_info_treasury", "\u00A76Treasury: \u00A7e%value% coins");
        EN.put("gui_info_rank", "\u00A76Clan Rank: \u00A7d%value%");
        EN.put("gui_info_title_clan", "\u00A76Clan Title: \u00A7d%value%");
        EN.put("gui_info_level", "\u00A76Clan Level: \u00A7a%value%");
        EN.put("gui_info_regions", "\u00A76Regions: \u00A7f%value%");
        EN.put("gui_info_homes", "\u00A76Home Points: \u00A7f%value%");
        EN.put("gui_info_stats", "\u00A76Wins: \u00A7a%wins% \u00A76Losses: \u00A7c%losses% \u00A76Deaths: \u00A74%deaths%");
        EN.put("gui_info_created", "\u00A76Created: \u00A77%value%");

        EN.put("gui_invite_title", "Invite to Clan");
        EN.put("gui_invite_content", "Enter the nickname of the player\nyou want to invite to your clan.\n\nThe player must be online.");
        EN.put("gui_invite_input", "Player Nickname");
        EN.put("gui_invite_hint", "Enter nickname...");

        EN.put("gui_invite_received_title", "Clan Invitation");
        EN.put("gui_invite_received_content", "\u00A7eLeader of clan \u00A7b[%tag%] %name%\u00A7e,\ninvites you to join their clan!\n\n\u00A76Invited by: \u00A7a%leader%\n\nDo you want to join?");

        EN.put("gui_kick_title", "Kick from Clan");
        EN.put("gui_kick_content", "Select a member to kick from the clan:\n\n\u00A7c(You cannot kick yourself)");
        EN.put("gui_kick_confirm_title", "Confirm Kick");
        EN.put("gui_kick_confirm_content", "\u00A7eAre you sure you want to kick\n\u00A7c%player%\u00A7e from the clan?\n\nThis action cannot be undone!");

        EN.put("gui_rename_title", "Change Clan Name");
        EN.put("gui_rename_content", "PAYMENT INFORMATION:\n\nCost: 1500 coins + 5%% commission\nTotal: 1575 coins\n\nAfter changing the name, the information\nwill be processed and updated in ~10 seconds.\nYou will receive a notification when complete.");
        EN.put("gui_rename_input", "New Clan Name");
        EN.put("gui_rename_hint", "Enter new name...");

        EN.put("gui_description_title", "Add Clan Description");
        EN.put("gui_description_content", "Enter a description for your clan.\n\nRules:\n- Maximum 200 characters\n- Will be visible to all players\n\nThe description will be added in ~5 seconds.\nYou will receive a notification when complete.");
        EN.put("gui_description_input", "Description");
        EN.put("gui_description_hint", "Enter clan description...");

        EN.put("gui_deposit_title", "Deposit to Treasury");
        EN.put("gui_deposit_content", "Current treasury: \u00A7e%amount% coins\n\nSelect amount to deposit:\n\u00A7c(Commission: 1-10%% will be charged)");
        EN.put("gui_deposit_custom_title", "Custom Deposit");
        EN.put("gui_deposit_custom_content", "Enter the amount you want to deposit.\n\nRules:\n- Minimum: 1,000 coins\n- Maximum: 10,000,000 coins\n\n\u00A7cCommission: 1-10%% will be charged");
        EN.put("gui_deposit_input", "Amount");
        EN.put("gui_deposit_hint", "Enter amount...");

        EN.put("gui_withdraw_title", "Withdraw from Treasury");
        EN.put("gui_withdraw_content", "Current treasury: \u00A7e%amount% coins\n\nSelect amount to withdraw:");
        EN.put("gui_withdraw_custom_title", "Custom Withdraw");
        EN.put("gui_withdraw_custom_content", "Current treasury: \u00A7e%treasury% coins\n\nEnter the amount you want to withdraw.\n\nRules:\n- Minimum: 1,000 coins\n- Maximum: %treasury% coins");

        EN.put("gui_chat_title", "Clan Chat");
        EN.put("gui_chat_content", "\u00A76Last message:\n%lastmsg%\n\nWrite your message below.\nOnly clan members will see it.");
        EN.put("gui_chat_no_messages", "\u00A77No messages yet");
        EN.put("gui_chat_input", "Message");
        EN.put("gui_chat_hint", "Type your message...");
        EN.put("gui_chat_format", "\u00A76[Clan Chat] \u00A7e[%role%] \u00A7b%player%\u00A7f: %message%");

        EN.put("gui_delete_title", "Delete Clan");
        EN.put("gui_delete_content", "\u00A7c\u00A7lWARNING!\n\n\u00A7fYou are about to delete the clan:\n\u00A7e%name% \u00A7f[\u00A7b%tag%\u00A7f]\n\n\u00A7cThis action is IRREVERSIBLE!\n\u00A7cAll clan data will be permanently deleted:\n\u00A7c- All members will be removed\n\u00A7c- Treasury will be lost\n\u00A7c- Regions will be unclaimed\n\n\u00A7fAre you sure?");

        RU.put("prefix", "\u00A7e[ClanGuard] ");
        RU.put("prefix_clans", "\u00A7e[КЛАНЫ] ");
        RU.put("error_not_in_clan", "\u00A7cВы не состоите в клане!");
        RU.put("error_not_leader", "\u00A7cТолько лидер клана может это сделать!");
        RU.put("error_already_in_clan", "\u00A7cВы уже состоите в клане: ");
        RU.put("error_clan_exists", "\u00A7cКлан с таким названием уже существует!");
        RU.put("error_tag_exists", "\u00A7cКлан с таким TAG уже существует!");
        RU.put("error_player_offline", "\u00A7cИгрок не в сети!");
        RU.put("error_player_in_clan", "\u00A7cЭтот игрок уже состоит в клане!");
        RU.put("error_invite_pending", "\u00A7cУ этого игрока уже есть приглашение!");
        RU.put("error_invite_expired", "\u00A7cПриглашение истекло!");
        RU.put("error_clan_full", "\u00A7cКлан переполнен!");
        RU.put("error_empty_name", "\u00A7cНазвание клана не может быть пустым!");
        RU.put("error_empty_tag", "\u00A7cTAG клана не может быть пустым!");
        RU.put("error_empty_message", "\u00A7cСообщение не может быть пустым!");
        RU.put("error_empty_description", "\u00A7cОписание не может быть пустым!");
        RU.put("error_invalid_name", "\u00A7cНеверное название! 3-20 символов, только буквы/цифры/подчёркивание.");
        RU.put("error_invalid_tag", "\u00A7cНеверный TAG! 3-6 символов, начинается с заглавной, только буквы/цифры.");
        RU.put("error_description_long", "\u00A7cОписание слишком длинное! Максимум 200 символов.");
        RU.put("error_message_long", "\u00A7cСообщение слишком длинное! Максимум 256 символов.");
        RU.put("error_amount_invalid", "\u00A7cНеверная сумма!");
        RU.put("error_amount_min", "\u00A7cМинимальная сумма 1,000 монет!");
        RU.put("error_amount_max_deposit", "\u00A7cМаксимальный депозит 10,000,000 монет!");
        RU.put("error_treasury_empty", "\u00A7cКазна пуста!");
        RU.put("error_treasury_insufficient", "\u00A7cНедостаточно монет в казне! Доступно: ");
        RU.put("error_economy_disabled", "\u00A7cИнтеграция с экономикой отключена! EconomyAPI не найден.");
        RU.put("error_no_members_kick", "\u00A7cНет участников для исключения!");
        RU.put("error_cannot_invite_self", "\u00A7cВы не можете пригласить себя!");
        RU.put("error_failed", "\u00A7cОперация не удалась. Попробуйте снова.");
        RU.put("error_console_only", "Эта команда доступна только в игре.");

        RU.put("success_clan_created", "\u00A7aКлан \"\u00A7e%name%\u00A7a\" с TAG [\u00A7b%tag%\u00A7a] был создан!");
        RU.put("success_clan_renamed", "\u00A7aВаш клан успешно переименован в \"\u00A7e%name%\u00A7a\"!");
        RU.put("success_description_added", "\u00A7aОписание клана успешно добавлено!");
        RU.put("success_joined_clan", "\u00A7aВы успешно вступили в клан!");
        RU.put("success_invite_sent", "\u00A7aПриглашение отправлено игроку \u00A7e%player%\u00A7a!");
        RU.put("success_invite_declined", "\u00A7eВы отклонили приглашение.");
        RU.put("success_player_kicked", "\u00A7a%player% \u00A7aбыл исключён из клана!");
        RU.put("success_kick_cancelled", "\u00A7eИсключение отменено.");
        RU.put("success_clan_deleted", "\u00A7cКлан был удалён!");
        RU.put("success_delete_cancelled", "\u00A7aУдаление клана отменено.");
        RU.put("success_deposit", "\u00A7aВы внесли \u00A7e%amount% монет\u00A7a в казну! (Комиссия: \u00A7c%commission%\u00A7a)");
        RU.put("success_withdraw", "\u00A7aВы сняли \u00A7e%amount% монет\u00A7a из казны!");

        RU.put("broadcast_clan_created", "\u00A7aИгрок \u00A7b%player% \u00A7aсоздал клан [\u00A7b%tag%\u00A7a], поздравляем!");
        RU.put("broadcast_member_joined", "\u00A7aНовый участник \u00A7b%player% \u00A7aвступил в наш клан, поздравляем!");
        RU.put("broadcast_member_kicked", "\u00A7c%player% \u00A7eбыл исключён из клана.");
        RU.put("broadcast_clan_deleted", "\u00A7cКлан [\u00A7b%tag%\u00A7c] был расформирован.");
        RU.put("broadcast_deposit", "\u00A7aУчастник \u00A7b%player% \u00A7aпожертвовал \u00A7e%amount% монет\u00A7a в казну клана!");
        RU.put("broadcast_withdraw", "\u00A7cУчастник клана \u00A7b%player%\u00A7c взял \u00A7e%amount% монет\u00A7c из казны клана.");

        RU.put("notify_kicked", "\u00A7cВы были исключены из клана!");
        RU.put("notify_clan_deleted", "\u00A7cВаш клан [\u00A7b%tag%\u00A7c] был удалён лидером.");
        RU.put("notify_invite_declined", "\u00A7e%player% \u00A7cотклонил ваше приглашение.");
        RU.put("notify_processing", "\u00A7eОбработка... Вы получите уведомление по завершении.");

        RU.put("gui_main_title", "Панель Клана");
        RU.put("gui_main_content", "ВАЖНАЯ ИНФОРМАЦИЯ!\n\nСоздание клана БЕСПЛАТНО.\nОднако при пополнении казны клана\nбудет взиматься комиссия от 1%% до 10%% от суммы.");
        RU.put("gui_btn_create", "Создать Клан");
        RU.put("gui_btn_info", "Информация о Клане");
        RU.put("gui_btn_invite", "Пригласить в Клан");
        RU.put("gui_btn_kick", "Исключить из Клана");
        RU.put("gui_btn_deposit", "Пополнить Казну");
        RU.put("gui_btn_withdraw", "Снять из Казны");
        RU.put("gui_btn_chat", "Клановый Чат");
        RU.put("gui_btn_rename", "Изменить Название");
        RU.put("gui_btn_description", "Добавить Описание");
        RU.put("gui_btn_delete", "Удалить Клан");
        RU.put("gui_btn_close", "Закрыть");
        RU.put("gui_btn_join", "Вступить");
        RU.put("gui_btn_decline", "Отказаться");
        RU.put("gui_btn_kick_confirm", "Исключить");
        RU.put("gui_btn_cancel", "Отмена");
        RU.put("gui_btn_delete_confirm", "\u00A7cУдалить Клан");
        RU.put("gui_btn_custom_amount", "\u00A7aСвоя Сумма");

        RU.put("gui_create_title", "Создание Клана");
        RU.put("gui_create_content", "Введите название и TAG для вашего клана.\n\nПравила TAG:\n- От 3 до 6 символов\n- Только буквы и цифры\n- Без пробелов и спецсимволов\n- Начинается с заглавной буквы");
        RU.put("gui_create_name", "Название Клана");
        RU.put("gui_create_name_hint", "Введите название...");
        RU.put("gui_create_tag", "TAG Клана");
        RU.put("gui_create_tag_hint", "Пример: ABC");

        RU.put("gui_info_title", "Инфо о Клане - %name%");
        RU.put("gui_info_leader", "\u00A76Лидер Клана: \u00A7e%value%");
        RU.put("gui_info_tag", "\u00A76Тег: \u00A7b[%value%]");
        RU.put("gui_info_description", "\u00A76Описание: \u00A7f%value%");
        RU.put("gui_info_members", "\u00A76Участники: \u00A7a%count% \u00A77/ \u00A7c%max%");
        RU.put("gui_info_treasury", "\u00A76Казна: \u00A7e%value% монет");
        RU.put("gui_info_rank", "\u00A76Ранг Клана: \u00A7d%value%");
        RU.put("gui_info_title_clan", "\u00A76Титул Клана: \u00A7d%value%");
        RU.put("gui_info_level", "\u00A76Уровень Клана: \u00A7a%value%");
        RU.put("gui_info_regions", "\u00A76Регионы: \u00A7f%value%");
        RU.put("gui_info_homes", "\u00A76Точки Дома: \u00A7f%value%");
        RU.put("gui_info_stats", "\u00A76Побед: \u00A7a%wins% \u00A76Поражений: \u00A7c%losses% \u00A76Смертей: \u00A74%deaths%");
        RU.put("gui_info_created", "\u00A76Создан: \u00A77%value%");

        RU.put("gui_invite_title", "Пригласить в Клан");
        RU.put("gui_invite_content", "Введите никнейм игрока,\nкоторого хотите пригласить в клан.\n\nИгрок должен быть онлайн.");
        RU.put("gui_invite_input", "Никнейм Игрока");
        RU.put("gui_invite_hint", "Введите никнейм...");

        RU.put("gui_invite_received_title", "Приглашение в Клан");
        RU.put("gui_invite_received_content", "\u00A7eЛидер клана \u00A7b[%tag%] %name%\u00A7e\nприглашает вас вступить в их клан!\n\n\u00A76Пригласил: \u00A7a%leader%\n\nХотите вступить?");

        RU.put("gui_kick_title", "Исключить из Клана");
        RU.put("gui_kick_content", "Выберите участника для исключения:\n\n\u00A7c(Вы не можете исключить себя)");
        RU.put("gui_kick_confirm_title", "Подтверждение");
        RU.put("gui_kick_confirm_content", "\u00A7eВы уверены, что хотите исключить\n\u00A7c%player%\u00A7e из клана?\n\nЭто действие нельзя отменить!");

        RU.put("gui_rename_title", "Изменить Название");
        RU.put("gui_rename_content", "ИНФОРМАЦИЯ ОБ ОПЛАТЕ:\n\nСтоимость: 1500 монет + 5%% комиссия\nИтого: 1575 монет\n\nПосле изменения названия информация\nбудет обработана за ~10 секунд.\nВы получите уведомление по завершении.");
        RU.put("gui_rename_input", "Новое Название");
        RU.put("gui_rename_hint", "Введите новое название...");

        RU.put("gui_description_title", "Добавить Описание");
        RU.put("gui_description_content", "Введите описание для вашего клана.\n\nПравила:\n- Максимум 200 символов\n- Будет видно всем игрокам\n\nОписание будет добавлено за ~5 секунд.\nВы получите уведомление по завершении.");
        RU.put("gui_description_input", "Описание");
        RU.put("gui_description_hint", "Введите описание клана...");

        RU.put("gui_deposit_title", "Пополнить Казну");
        RU.put("gui_deposit_content", "Текущая казна: \u00A7e%amount% монет\n\nВыберите сумму для внесения:\n\u00A7c(Комиссия: 1-10%% будет удержана)");
        RU.put("gui_deposit_custom_title", "Своя Сумма");
        RU.put("gui_deposit_custom_content", "Введите сумму для внесения.\n\nПравила:\n- Минимум: 1,000 монет\n- Максимум: 10,000,000 монет\n\n\u00A7cКомиссия: 1-10%% будет удержана");
        RU.put("gui_deposit_input", "Сумма");
        RU.put("gui_deposit_hint", "Введите сумму...");

        RU.put("gui_withdraw_title", "Снять из Казны");
        RU.put("gui_withdraw_content", "Текущая казна: \u00A7e%amount% монет\n\nВыберите сумму для снятия:");
        RU.put("gui_withdraw_custom_title", "Своя Сумма");
        RU.put("gui_withdraw_custom_content", "Текущая казна: \u00A7e%treasury% монет\n\nВведите сумму для снятия.\n\nПравила:\n- Минимум: 1,000 монет\n- Максимум: %treasury% монет");

        RU.put("gui_chat_title", "Клановый Чат");
        RU.put("gui_chat_content", "\u00A76Последнее сообщение:\n%lastmsg%\n\nНапишите ваше сообщение ниже.\nЕго увидят только участники клана.");
        RU.put("gui_chat_no_messages", "\u00A77Сообщений пока нет");
        RU.put("gui_chat_input", "Сообщение");
        RU.put("gui_chat_hint", "Введите сообщение...");
        RU.put("gui_chat_format", "\u00A76[Клан Чат] \u00A7e[%role%] \u00A7b%player%\u00A7f: %message%");

        RU.put("gui_delete_title", "Удалить Клан");
        RU.put("gui_delete_content", "\u00A7c\u00A7lВНИМАНИЕ!\n\n\u00A7fВы собираетесь удалить клан:\n\u00A7e%name% \u00A7f[\u00A7b%tag%\u00A7f]\n\n\u00A7cЭто действие НЕОБРАТИМО!\n\u00A7cВсе данные клана будут удалены:\n\u00A7c- Все участники будут удалены\n\u00A7c- Казна будет потеряна\n\u00A7c- Регионы будут сняты\n\n\u00A7fВы уверены?");
    }

    private Lang() {
    }

    public static void init() {
        String lang = Server.getInstance().getLanguage().getLang();
        isRussian = lang.equalsIgnoreCase("rus") || lang.equalsIgnoreCase("ru");
    }

    public static String get(String key) {
        Map<String, String> map = isRussian ? RU : EN;
        return map.getOrDefault(key, EN.getOrDefault(key, key));
    }

    public static String get(String key, String... replacements) {
        String msg = get(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return msg;
    }

    public static boolean isRussian() {
        return isRussian;
    }
}
