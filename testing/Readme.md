## Project Documentation

### Files in this folder

#### plot_rps.py
This script is responsible for plotting the requests per second (RPS) data. It reads the data from a artyllery

#### load_test_generator.py
This script is used to generate load tests. It simulates multiple users accessing the system simultaneously to test its performance under stress. The generated data can be used to identify bottlenecks and optimize the system's performance.

#### load_test.yml
This script is used as a base file to describe what are is the basic experiment.

### Requirements

To run the scripts in this folder, you need the following dependencies:
-node
 -npm install -g artillery
- Python 3.x
- matplotlib
- pandas
- yaml
- requests

You can install the required packages using the following command:

```sh
npm install -g artillery
pip install matplotlib pandas pyyaml requests
```

running
```sh
 artillery run load_test.yml
 python plot_rps.py
```
