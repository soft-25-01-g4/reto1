import numpy as np
import yaml

# Define test durations (in seconds)
stochastic_duration = 60  # 1 min stochastic load

# Define request rates
lambda_rps = 17  # Average requests per second during stochastic phase

# Generate stochastic RPS values using Poisson distribution


# Define test phases
phases = [
    # Start slow
]

# Add Stochastic Load Phase (Poisson-distributed traffic)
for i in range(1,5):
    stochastic_rps_values = np.random.poisson(lam=lambda_rps, size=stochastic_duration)
    for rps in stochastic_rps_values:
        phases.append({"name": "Stochastic latency Load Phase", "duration": 1, "arrivalRate": int(rps)})
    phases.append({"name": "Stochastic  UnLoad", "duration": 60, "arrivalRate": 0})


# Add Spike Load Phase (sudden traffic jump)
#phases.append({"name": "Spike Load Phase", "duration": spike_duration, "arrivalRate": spike_rps})

# Add Cool-down Phase (gradually decrease traffic)

# Create Artillery test config
artillery_config = {
    "config": {
        "variables":{"category":["apl","rht","ibm","aws"]}
        ,"target": "https://order-juank1400-dev.apps.rm3.7wse.p1.openshiftapps.com",
       "phases": phases
    },
    "scenarios": [
        {
            "flow": [
                {"post": {
                    "url": "/orders",
                        "json":{
                        "id": "{{ $randomNumber(1350000001, 100000000) }}", # ID instead of userId
                        "type":"buy",
                        "asset": "{{category}}",
                        "quantity": "{{$randomNumber(3,14)}}"  #
                    },
                    "headers": {"Content-Type": "application/json"}
                }}
            ]
        }
    ]
}

# Save to YAML
with open("stochastic_latency_load_test.yml", "w") as f:
    yaml.dump(artillery_config, f, default_flow_style=False)

print("✅ Stochastic load test YAML generated: stochastic_latency_load_test.yml")
