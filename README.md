[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FRUSPlugins-Team&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

# 🛡️ ClanGuard

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-Bedrock%20Edition-brightgreen?style=for-the-badge" alt="Minecraft Bedrock">
  <img src="https://img.shields.io/badge/Platform-Nukkit-blue?style=for-the-badge" alt="Nukkit">
  <img src="https://img.shields.io/badge/Java-8-orange?style=for-the-badge" alt="Java 8">
  <img src="https://img.shields.io/badge/Version-1.0.0-red?style=for-the-badge" alt="Version">
</p>

<p align="center">
  <b>Advanced Clan Management & Protection Plugin for Nukkit</b><br>
  <i>Продвинутый плагин управления кланами с уникальной системой защиты</i>
</p>

---

## 📋 Description | Описание

**English:**
ClanGuard is an advanced clan management plugin for Minecraft Bedrock Edition servers running on Nukkit. It features a unique protection system with 64-character Clan IDs and 32-character Player IDs (PID), making it impossible to steal clans even if an account is compromised.

**Русский:**
ClanGuard — это продвинутый плагин управления кланами для серверов Minecraft Bedrock Edition на платформе Nukkit. Плагин имеет уникальную систему защиты с 64-значными ID кланов и 32-значными PID игроков, что делает невозможным кражу клана даже при компрометации аккаунта.

---

## ✨ Features | Возможности

### 🔐 Security | Безопасность
| Feature | Description |
|---------|-------------|
| **64-char Clan ID** | Unique secret key for clan recovery |
| **32-char PID** | Personal ID for each clan member |
| **IP + Nick + PID** | Triple verification of ownership |
| **Console-only ID** | Clan ID visible only in server console |

### 🎮 Clan Management | Управление кланом
- ✅ Create clans (FREE)
- ✅ Clan information panel
- ✅ Invite players
- ✅ Kick members
- ✅ Change clan name (paid)
- ✅ Add clan description
- ✅ Delete clan

### 💰 Treasury | Казна
- ✅ Deposit coins (1-10% commission)
- ✅ Withdraw coins (leader only)
- ✅ Custom amounts support
- ✅ EconomyAPI integration

### 💬 Communication | Общение
- ✅ Private clan chat
- ✅ Broadcast notifications
- ✅ Last message display

### 🌐 Localization | Локализация
- ✅ English language
- ✅ Russian language (Русский)
- ✅ Auto-detection based on server settings

---

## 📸 Screenshots | Скриншоты

<details>
<summary>Click to expand | Нажмите для просмотра</summary>

### Main Panel | Главная панель
```
┌─────────────────────────────┐
│        Clan Panel           │
├─────────────────────────────┤
│ • Create Clan               │
│ • Clan Info                 │
│ • Invite to Clan            │
│ • Kick from Clan            │
│ • Deposit to Treasury       │
│ • Withdraw from Treasury    │
│ • Clan Chat                 │
│ • Change Name               │
│ • Add Description           │
│ • Delete Clan               │
└─────────────────────────────┘
```

### Clan Info | Информация о клане
```
┌─────────────────────────────┐
│    Clan Info - MyClan       │
├─────────────────────────────┤
│ Leader: PlayerName          │
│ Tag: [MCL]                  │
│ Description: Our clan!      │
│ Members: 5 / 150            │
│ Treasury: 50,000 coins      │
│ Level: 1                    │
│ Wins: 10 Losses: 2          │
│ Created: 2024-01-15         │
└─────────────────────────────┘
```

</details>

---

## 📥 Installation | Установка

### Requirements | Требования
- Nukkit server (API 1.0.0+)
- Java 8+
- (Optional) EconomyAPI for treasury functions

### Steps | Шаги

1. **Download** the latest `ClanGuard.jar` from [Releases](https://github.com/RUSPlugins-Team/ClanGuard/releases)

2. **Place** the JAR file in your server's `plugins/` folder

3. **Restart** your server

4. **Done!** Use `/clan` command in-game

```bash
# Server structure
server/
├── plugins/
│   ├── ClanGuard.jar    ← Place here
│   └── EconomyAPI.jar   ← Optional
└── nukkit.jar
```

---

## 🎮 Commands | Команды

| Command | Description | Permission |
|---------|-------------|------------|
| `/clan` | Open clan panel | Everyone |

---

## 📁 Data Structure | Структура данных

```
plugins/
└── clanguard/
    └── YourClanName/
        ├── id.json       ← Secret clan ID + creator info
        ├── infoclan.db   ← Clan information
        ├── players.db    ← Member list (max 150)
        └── chat.db       ← Last chat message
```

### id.json
```json
{
  "clanId": "64-character-unique-id...",
  "creator": {
    "ip": "127.0.0.1",
    "nick": "PlayerName",
    "pid": "32-character-player-id..."
  }
}
```

### infoclan.db
```json
{
  "name": "MyClan",
  "tag": "MCL",
  "leader": "PlayerName",
  "createdAt": "2024-01-15 12:30:00",
  "treasury": 50000,
  "level": 1,
  "rank": "None",
  "title": "None",
  "description": "Best clan ever!"
}
```

### players.db
```json
{
  "maxPlayers": 150,
  "count": 3,
  "members": [
    {
      "nick": "PlayerName",
      "pid": "32-character-pid...",
      "role": "leader",
      "joinedAt": "2024-01-15 12:30:00"
    }
  ]
}
```

---

## 💰 Economy | Экономика

### Commission System | Система комиссий

| Deposit Amount | Commission |
|----------------|------------|
| 10,000 | ~1% |
| 100,000 | ~4% |
| 1,000,000 | ~7% |
| 10,000,000 | 10% |

### Prices | Цены

| Action | Cost |
|--------|------|
| Create Clan | FREE |
| Change Name | 1,575 coins (1,500 + 5%) |
| Deposit | Amount - Commission |
| Withdraw | FREE (leader only) |

---

## 🔧 EconomyAPI Integration

If EconomyAPI is not installed:
- Plugin will **NOT crash**
- Warning message in console
- Treasury functions will be disabled
- Message to players: "Economy integration is disabled!"

```
[WARN] EconomyAPI not found! Treasury functions will be disabled.
```

---

## 🌍 Language Support | Языковая поддержка

The plugin automatically detects server language:

| Server Language | Plugin Language |
|-----------------|-----------------|
| `rus` / `ru` | Русский |
| Any other | English |

Configure in `nukkit.yml`:
```yaml
settings:
  language: "rus"  # For Russian
  # language: "eng"  # For English
```

---

## 🛡️ Clan Recovery | Восстановление клана

If someone steals your account:

1. Contact server administration
2. Provide your **64-character Clan ID** (from server console logs)
3. Admin verifies your IP + Nick + PID
4. Clan ownership restored!

**The Clan ID is only visible in server console, never in-game!**

---

## 📊 Technical Specifications

| Specification | Value |
|---------------|-------|
| API Version | 1.0.0 |
| Java Version | 8+ |
| Max Members | 150 per clan |
| Clan ID Length | 64 characters |
| Player ID Length | 32 characters |
| Max Tag Length | 3-6 characters |
| Max Name Length | 3-20 characters |
| Max Description | 200 characters |

---

## 🚀 Roadmap | Планы

- [ ] Officer role with permissions
- [ ] Transfer leadership
- [ ] Clan regions/territories
- [ ] Clan home points
- [ ] Clan wars system
- [ ] Clan levels and ranks
- [ ] Alliance system
- [ ] Web panel

---

## 🤝 Contributing | Вклад

Contributions are welcome! | Мы приветствуем вклад в проект!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License | Лицензия

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors | Авторы

**RUSPlugins-Team**
- GitHub: [@RUSPlugins-Team](https://github.com/RUSPlugins-Team)

---

## ⭐ Support | Поддержка

If you like this plugin, please give it a ⭐ star on GitHub!

Если вам нравится этот плагин, поставьте ⭐ звезду на GitHub!

---

## 📞 Contact | Контакты

- **Issues:** [GitHub Issues](https://github.com/RUSPlugins-Team/ClanGuard/issues)
- **Discussions:** [GitHub Discussions](https://github.com/RUSPlugins-Team/ClanGuard/discussions)

---

<p align="center">
  Made with ❤️ by <b>RUSPlugins-Team</b>
</p>

<p align="center">
  <img src="https://img.shields.io/github/stars/RUSPlugins-Team/ClanGuard?style=social" alt="Stars">
  <img src="https://img.shields.io/github/forks/RUSPlugins-Team/ClanGuard?style=social" alt="Forks">
  <img src="https://img.shields.io/github/watchers/RUSPlugins-Team/ClanGuard?style=social" alt="Watchers">
</p>
