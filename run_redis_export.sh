#!/bin/bash

# Parámetros
POD_NAME="redis-1-pn6fk"          # Nombre del pod de Redis
NAMESPACE="juank1400-dev"           # Espacio de nombres en Kubernetes
REDIS_PASSWORD="3KYSOsSYNwcKrPQ3" # Reemplázala con tu contraseña real
EJECUCION="0747"              # Cambia esto si necesitas un identificador único

# Nombre del script dentro del pod
SCRIPT_NAME="redis_export.sh"
REMOTE_PATH="/tmp/$SCRIPT_NAME"
OUTPUT_FILE="/tmp/redis-export_$EJECUCION.txt"

# 1. Crear el script de exportación temporalmente
cat <<EOF > $SCRIPT_NAME
#!/bin/sh
export REDIS_PASSWORD="$REDIS_PASSWORD"
redis-cli -a "\$REDIS_PASSWORD" --scan | while read key; do 
  type=\$(redis-cli -a "\$REDIS_PASSWORD" TYPE "\$key")

  if [ "\$type" = "string" ]; then
    value=\$(redis-cli -a "\$REDIS_PASSWORD" GET "\$key")
    echo "\$key \$value" >> $OUTPUT_FILE

  elif [ "\$type" = "hash" ]; then
    echo -n "\$key" >> $OUTPUT_FILE
    redis-cli -a "\$REDIS_PASSWORD" HGETALL "\$key" | awk 'NR%2==1 {printf " %s", \$0} NR%2==0 {printf " %s", \$0} END {print ""}' >> $OUTPUT_FILE
  fi
done
EOF

# 2. Subir el script al pod
kubectl cp $SCRIPT_NAME $POD_NAME:$REMOTE_PATH -n $NAMESPACE
kubectl exec -n $NAMESPACE $POD_NAME -- chmod +x $REMOTE_PATH

# 3. Ejecutar el script dentro del pod
kubectl exec -n $NAMESPACE $POD_NAME -- sh $REMOTE_PATH

# 4. Descargar el archivo de salida
kubectl cp $POD_NAME:$OUTPUT_FILE ./redis-export_$EJECUCION.txt -n $NAMESPACE

# 5. Limpiar la base de datos de Redis
kubectl exec -n $NAMESPACE $POD_NAME -- redis-cli -a "$REDIS_PASSWORD" FLUSHALL

# 6. Limpiar archivos temporales dentro del pod
kubectl exec -n $NAMESPACE $POD_NAME -- rm -f $REMOTE_PATH $OUTPUT_FILE

# 7. Eliminar el script local
rm -f $SCRIPT_NAME

echo "✅ Exportación completada. Archivo descargado como redis-export_$EJECUCION.txt"
