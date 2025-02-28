import numpy as np
import yaml

# Define test durations (in seconds)
warmup_duration = 3*60       # 5 min warm-up
stochastic_duration = 30*60  # 30 min stochastic load
spike_duration = 60        # 5 min spike load
cooldown_duration = 2*60    # 5 min cool-down

# Define request rates
warmup_rps = 20    # Start slow
lambda_rps = 84   # Average requests per second during stochastic phase
spike_rps = 300    # High traffic spike
cooldown_rps = 20  # Gradual decrease

# Generate stochastic RPS values using Poisson distribution
stochastic_rps_values = np.random.poisson(lam=lambda_rps, size=stochastic_duration)

# Define test phases
phases = [
    {"name": "Warm-up Phase", "duration": warmup_duration, "arrivalRate": warmup_rps},  # Start slow
]

# Add Stochastic Load Phase (Poisson-distributed traffic)
for rps in stochastic_rps_values:
    phases.append({"name": "Stochastic Load Phase", "duration": 1, "arrivalRate": int(rps)})

# Add Spike Load Phase (sudden traffic jump)
#phases.append({"name": "Spike Load Phase", "duration": spike_duration, "arrivalRate": spike_rps})

# Add Cool-down Phase (gradually decrease traffic)
phases.append({"name": "Cool-down Phase", "duration": cooldown_duration, "arrivalRate": cooldown_rps})

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
                        "id": "{{ $randomNumber(1200000001, 1300000000) }}", # ID instead of userId
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
with open("stochastic_load_test.yml", "w") as f:
    yaml.dump(artillery_config, f, default_flow_style=False)

print("✅ Stochastic load test YAML generated: stochastic_load_test.yml")
