#!/bin/sh
export REDIS_PASSWORD="3KYSOsSYNwcKrPQ3"
redis-cli -a "$REDIS_PASSWORD" --scan | while read key; do 
  type=$(redis-cli -a "$REDIS_PASSWORD" TYPE "$key")

  if [ "$type" = "string" ]; then
    value=$(redis-cli -a "$REDIS_PASSWORD" GET "$key")
    echo "$key $value" >> /tmp/redis-export_0747.txt

  elif [ "$type" = "hash" ]; then
    echo -n "$key" >> /tmp/redis-export_0747.txt
    redis-cli -a "$REDIS_PASSWORD" HGETALL "$key" | awk 'NR%2==1 {printf " %s", $0} NR%2==0 {printf " %s", $0} END {print ""}' >> /tmp/redis-export_0747.txt
  fi
done
