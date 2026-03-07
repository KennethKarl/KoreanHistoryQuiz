#!/usr/bin/env bash
# Telegram 알림 전송 스크립트
# 환경변수: TELEGRAM_BOT_TOKEN, TELEGRAM_CHAT_ID
MESSAGE="${1:-알림}"
if [[ -z "$TELEGRAM_BOT_TOKEN" || -z "$TELEGRAM_CHAT_ID" ]]; then
  echo "[notify-telegram] 환경변수 TELEGRAM_BOT_TOKEN / TELEGRAM_CHAT_ID 미설정 — 알림 스킵"
  exit 0
fi
curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
  -d "chat_id=${TELEGRAM_CHAT_ID}" \
  -d "text=${MESSAGE}" \
  -d "parse_mode=Markdown" > /dev/null
echo "[notify-telegram] 전송 완료: $MESSAGE"
