import pandas as pd
import matplotlib.pyplot as plt

# Cargar el archivo de logs
file_path = "./redis-export.txt"  # Ajusta con la ruta correcta
data = []

import pandas as pd
import matplotlib.pyplot as plt

# Convert timestamps to datetime format
df["Start Time"] = pd.to_datetime(df["Start Time"], unit="ms")
df["Start Time"] = df["Start Time"].dt.strftime("%H:%M:%S")  # Format HH:MM:SS

# Remove rows where End Time is missing (to avoid NaN issues)
df = df.dropna(subset=["Response Time"])

# Plot Start Time vs. Response Time
plt.figure(figsize=(12, 6))
plt.plot(df["Start Time"], df["Response Time"], marker="o", linestyle="-", color="b", label="Response Time (ms)")

# Formatting
plt.xlabel("Start Time (HH:MM:SS)")
plt.ylabel("Response Time (ms)")
plt.title("Response Time vs Start Time")
plt.xticks(rotation=45)
plt.grid(True, linestyle="--", alpha=0.6)
plt.legend()

# Show plot
plt.show()

