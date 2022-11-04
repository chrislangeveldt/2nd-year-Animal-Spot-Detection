# How to Run the Marker

## Pre-requisites
This script requires the `pillow` module for python3. If you are programming on
NARGA, this is already installed for you. If you wish to run this from home,
you will first need to install the requirements:

```bash
pip3 install -r requirements.txt
```

## Execution
Use the following command:

```bash
python3 testscript <mode>
```

The modes are as follows:

* **0** -- Greyscale
* **1** -- Noise reduction
* **2** -- Edge detection
* **3** -- Spot detection
* **4** -- Errors
* **5**/'**all**' -- All
