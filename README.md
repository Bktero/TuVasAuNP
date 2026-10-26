# Tu vas au NP ?

## Developer

Create your `.env` file with `cp .env.example .env` and set the values. Your can also set these variables directly in your environment.

Run the application: `./gradlew run`

Update grade itself:  `./gradlew wrapper --gradle-version 9.6.1`

### Linter

To check the code: `./gradlew ktlintCheck`. It's automatically added to `check`.

To autocorrect the code: `./gradlew ktlintFormat`

## Raspberry

```zsh
 % ssh pierre@192.168.1.137

pierre@192.168.1.137's password: 
Linux pi 6.12.47+rpt-rpi-v6 #1 Raspbian 1:6.12.47-1+rpt1 (2025-09-16) armv6l

The programs included with the Debian GNU/Linux system are free software;
the exact distribution terms for each program are described in the
individual files in /usr/share/doc/*/copyright.

Debian GNU/Linux comes with ABSOLUTELY NO WARRANTY, to the extent
permitted by applicable law.
Last login: Sun Oct 11 16:18:15 2026 from 192.168.1.229
pierre@pi:~ $
```

```bash
$ systemctl list-units --type=service | grep np
  tuvasaunpbot.service                                        loaded active running Telegram bot to download NP maps, written in Kotlin
~ $ sudo systemctl show  tuvasaunpbot.service
Type=simple
ExitType=main
Restart=always
RestartMode=normal
NotifyAccess=none
RestartUSec=5s
RestartSteps=0
...
ExecMainStatus=0
ExecStart={ path=/home/pierre/run_TuVasAuNpBot.sh ; argv[]=/home/pierre/run_TuVasAuNpBot.sh ; ignore_errors=no ; start_time=[Tue 2026-10-06 16:42:26 CEST] ; stop_time=[n/a] ; pid=1114 ; code=(null) ; status=0/0 }
ExecStartEx={ path=/home/pierre/run_TuVasAuNpBot.sh ; argv[]=/home/pierre/run_TuVasAuNpBot.sh ; flags= ; start_time=[Tue 2026-10-06 16:42:26 CEST] ; stop_time=[n/a] ; pid=1114 ; code=(null) ; status=0/0 }
Slice=system.slice
$ sudo systemctl stop tuvasaunpbot.service
```
