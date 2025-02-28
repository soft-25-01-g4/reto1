import json
import matplotlib.pyplot as plt
import pandas as pd
import datetime

# Load Artillery results file
with open("resultsFINAL_lat_3pm.json") as f:
    data = json.load(f)

# Extract request rate, error rate, and latency per phase
timestamps = []
rps_values = []
error_values = []
latency_values = []

for phase in data.get("intermediate", []):
    timestamp = phase.get("firstCounterAt", 0)  # Start time in ms
    rps = phase.get("rates", {}).get("http.request_rate", 0)  # Requests per second
    errors = (
        phase.get("counters", {}).get("http.responses.4xx", 0) +
        phase.get("counters", {}).get("http.responses.5xx", 0)
    )  # Count of 4xx & 5xx errors
    latency = phase.get("histograms", {}).get("http.response_time", {}).get("p50", 0)  

    timestamps.append(datetime.datetime.fromtimestamp(timestamp / 1000))  # Convert ms to datetime
    rps_values.append(rps)
    error_values.append(errors)
    latency_values.append(latency)

# Convert to DataFrame for better handling
df = pd.DataFrame({"Time": timestamps, "RPS": rps_values, "Errors": error_values, "Latency": latency_values})

# Define bar width
bar_width = 0.3
x_indexes = range(len(df))

# Create bar chart
fig, ax = plt.subplots(figsize=(12, 6))

# Plot bars for each metric
#ax.bar(x_indexes, df["RPS"], width=bar_width, label="Requests Per Second", color="blue", alpha=0.7)
ax.bar([x + bar_width for x in x_indexes], df["Errors"], width=bar_width, label="Errors", color="red", alpha=0.7)
#ax.bar([x + 2 * bar_width for x in x_indexes], df["Latency"], width=bar_width, label="Latency (ms)", color="green", alpha=0.7)

# Formatting
ax.set_xlabel("Time (HH:MM:SS)")
ax.set_ylabel("Value")
ax.set_title("Artillery Load Test - Requests, Errors & Latency Over Time")

# Set x-axis ticks and labels
ax.set_xticks([x + bar_width for x in x_indexes])
ax.set_xticklabels(df["Time"].dt.strftime('%H:%M:%S'), rotation=45)

# Add legend and grid
plt.legend()
plt.grid(axis="y", linestyle="--", alpha=0.6)

# Show the chart
plt.tight_layout()
plt.show()
