import json
import matplotlib.pyplot as plt
import pandas as pd
import datetime

# Load Artillery results file
with open("firstrunresults3am.json") as f:
    data = json.load(f)

# Extract request rate and error rate per phase
timestamps = []
rps_values = []
error_values = []
latency_values = []


for phase in data.get("intermediate", []):
    
    timestamp = phase.get("firstCounterAt", 0)  # Start time of phase (in ms)
    rps = phase.get("rates", {}).get("http.request_rate", 0)  # Requests per second
    errors = phase.get("counters", {}).get("http.responses.4xx", 0) + phase.get("counters", {}).get("http.responses.5xx", 0)  # Count of 4xx & 5xx errors
    latency = phase.get("histograms", {}).get("http.response_time", {}).get("p50") 
       
    if latency <1000:
        timestamps.append(datetime.datetime.fromtimestamp(timestamp / 1000))  # Convert ms to datetime
        rps_values.append(rps)
        error_values.append(errors)
        latency_values.append(latency)


# Create figure and axis
fig, ax1 = plt.subplots(figsize=(12, 6))

# Plot RPS (left Y-axis)
#ax1.set_xlabel("Time (HH:MM:SS)")
#ax1.set_ylabel("Requests Per Second (RPS)", color='b')
#ax1.plot(timestamps, rps_values, marker='o', linestyle='-', color='b', label="RPS")
#ax1.tick_params(axis='y', labelcolor='b')
#
## Create second Y-axis for errors
#ax2 = ax1.twinx()
#ax2.set_ylabel("Error Rate (errors per second)", color='r')
#ax2.plot(timestamps, error_values, marker='s', linestyle='--', color='r', label="Error Rate")
#ax2.tick_params(axis='y', labelcolor='r')

# Create third Y-axis for latency
plt.yscale("log")
ax1.spines['right'].set_position(('outward', 60))  # Offset the third axis
ax1.set_ylabel("Latency (ms)", color='g')
ax1.plot(timestamps, latency_values, marker='^', linestyle='-.', color='g', label="Latency")
ax1.tick_params(axis='y', labelcolor='g')


# Formatting
plt.title("Artillery Load Test - Request Rate & Error Rate Over Time  & Latency Over Time")
ax1.xaxis.set_major_formatter(plt.matplotlib.dates.DateFormatter('%H:%M:%S'))  # Format time
fig.autofmt_xdate()  # Auto rotate time labels
fig.legend(loc="upper left")

# Show the graph
plt.grid(True, linestyle="--", alpha=0.6)
plt.tight_layout()
plt.show()
