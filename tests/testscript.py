import subprocess, threading, glob, codecs, re, os
from PIL import Image
from sys import argv, exit

import cases
BIN_PATH = "../bin/"
SRC_PATH = "../src/"
OUT_PATH = "../out/"
GS_TEST_PATH = "gs/"
NR_TEST_PATH = "nr/"
ED_TEST_PATH = "ed/"
SD_TEST_PATH = "sd/"
INPUT_PATH = "input/"
OUT_DIR = "../out/"
INVAL_TEST_PATH = "invalid/"

DISPLAY_INDIVIDUAL_RESULTS = True

# Percentage accuracy needed for marking
GREYSCALE_CRITERIA = 99.0
NOISE_REDUCTION_CRITERIA = 98.0
EDGE_DETECTION_CRITERIA = 98.5
SPOT_DETECTION_CRITERIA = 99.0
COUNT_APPROX = 85 # 80% must be correct (80 or 85 seems good)
PIXEL_THRESHOLD = 1 # pixels must be within this value from correct answer

def atoi(text):
    return int(text) if text.isdigit() else text

def natural_keys(text):
    return [atoi(c) for c in re.split(r'(\d+)', text)]

class Command:
    def __init__(self, cmd, cwd='./'):
        self.cmd = cmd
        self.cwd = cwd
        self.process = None

    def run(self, timeout=600):
        def target():
            self.process = subprocess.Popen(self.cmd, stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE, cwd=self.cwd, shell=True)
            self.out, self.err = self.process.communicate()

        thread = threading.Thread(target=target)
        thread.start()

        thread.join(timeout)
        if thread.is_alive():
            self.process.terminate()
            thread.join()

        return self.process.returncode, self.out.decode('utf-8'), \
                self.err.decode('utf-8')


def compare_pictures(file1, file2):
    img1 = None
    img2 = None
    try:
        img1 = Image.open(file1)
    except Exception as e:
        print("Unable to open image {}: {}".format(file1, e))
        return 0

    try:
        img2 = Image.open(file2)
    except Exception as e:
        print("Unable to open image {}: {}".format(file1, e))
        return 0

    try:
        img1 = img1.convert(mode="RGB")
    except Exception as e:
        print("Unable to convert image {} to RGB: {}".format(file1, e))
    
    try:
        img2 = img2.convert(mode="RGB")
    except Exception as e:
        print("Unable to convert image {} to RGB: {}".format(file2, e))

    total = 0
    correct = 0
    for i in range(img1.width):
        for j in range(img1.height):
            c1 = img1.getpixel((i,j))
            c2 = img2.getpixel((i,j))
            # Bypasses the alpha issue (variation if saved in ARGB or RGB)
            # Thresholding to deal with float rounding issues
            if  abs(c1[0] - c2[0]) <= PIXEL_THRESHOLD and \
                    abs(c1[1] -c2[1]) <= PIXEL_THRESHOLD and \
                    abs(c1[2] -c2[2]) <= PIXEL_THRESHOLD:
                correct = correct + 1
            total = total + 1
    percentage = 1.0 * correct / total * 100
    return percentage


def parse_tests(mode):
    tests = []
    out_cases = []
    if mode == 'gs':
        for s in cases.GS_CASES:
            outcase = os.path.splitext(s)[0] + "_GS.png"
            out_cases.append("" + outcase)
            tests.append(INPUT_PATH +  s)
    
    elif mode == 'nr':
        for s in cases.NR_CASES:
            outcase = os.path.splitext(s)[0] + "_NR.png"
            out_cases.append("" + outcase)
            tests.append(INPUT_PATH +  s)
    
    elif mode == 'ed':
        for item in cases.ED_CASES:
            s = item['file']
            outcase = os.path.splitext(s)[0] + "_ED.png"
            out_cases.append({'file' : ("" + outcase), 'eps' : item['eps']})
            tests.append(INPUT_PATH +  s)

    elif mode == 'sd':
        for item in cases.SD_CASES:
            s = item['file']
            outcase = os.path.splitext(s)[0] + "_SD.png"
            out_text = os.path.splitext(s)[0] + ".out"
            out_cases.append({'file' : ("" + outcase), 'eps' : item['eps'],
                'rl' : item['rl'], 'rm' : item['rm'], 'out': out_text})
            tests.append(INPUT_PATH +  s)
    
    else:
        print("Invalid mode " + mode)
        return None, None
    
    return tests, out_cases

def run_tests(bin_path, tests, outs, mode, char_threshold=200):
    if mode == "gs":
        print('#'*18, "GS", '#'*19)
    elif mode == 'nr':
        print('#'*18, "NR", '#'*19)
    elif mode == 'ed':
        print('#'*18, "ED", '#'*19)
    elif mode == 'sd':
        print('#'*18, "SD", '#'*19)
    else:
        print('#'*20, '#'*20)

    correct_count = 0
    total_count = len(tests)
    for test, out_case in zip(tests, outs):
        received = ""
        criteria = 0
        if mode == "gs" or mode == "nr":
            received = OUT_DIR + out_case
        else:
            received = OUT_DIR + out_case['file']
        expected = ""

        if mode == "gs":
            expected = GS_TEST_PATH + out_case
            criteria = GREYSCALE_CRITERIA
            cmd = Command("java -cp '{}' Animal 0 {}".format(
                bin_path, test), './')

        elif mode == "nr":
            criteria = NOISE_REDUCTION_CRITERIA
            expected = NR_TEST_PATH + out_case
            cmd = Command("java -cp '{}' Animal 1 {}".format(
                bin_path, test), './')
        
        elif mode == "ed":

            criteria = EDGE_DETECTION_CRITERIA
            expected = ED_TEST_PATH + out_case['file']
            cmd = Command("java -cp '{}' Animal 2 {} {}".format(
                bin_path, test, out_case['eps']), './')

        elif mode == "sd":
            criteria = SPOT_DETECTION_CRITERIA
            expected = SD_TEST_PATH + out_case['file']
            out_file = SD_TEST_PATH + out_case['out']
            cmd = Command("java -cp '{}' Animal 3 {} {} {} {}".format(
                bin_path, test, out_case['eps'], out_case['rl'],
                out_case['rm']), './')

        else:
            print("Invalid mode " + mode)
            return

        sig, out, err = cmd.run()

        if sig != 0:
            print('ERR', test)
            print('>>>>>>>> ERR >>>>>>>>')
            print(err)
            continue
        
        if mode == "sd":
            with open(out_file, 'r') as file:
                out_count = 0
                diff = 0
                try:
                    out_count = file.readline()
                    out_count = int(out_count)
                    out = int(out)
                    diff = abs(min(out_count, out) / max(out_count, out)) * 100
                except Exception as e:
                    print("ERR", test)
                    print("NO OUTPUT")
                    continue
                file.close()

        out = compare_pictures(expected, received)

        if mode == 'sd':
            if float(out) >= criteria and diff > COUNT_APPROX:
                correct_count += 1
                if DISPLAY_INDIVIDUAL_RESULTS:
                    print('✔', test, "\n\tpicture ratio: ", out, "\n\tspot ratio:    ", diff)
            else:
                print('X', test,"\n\tpicture ratio: ", out, "\n\tspot ratio:    ", diff)
        else:
            if float(out) >= criteria:
                correct_count += 1
                if DISPLAY_INDIVIDUAL_RESULTS:
                    print('✔', test, "\n\tpicture ratio:", out)
            else:
                print('X', test, "\n\tpicture ratio:", out)
        
        if (os.path.exists(received)):
            os.remove(received) # Comment this out if you wish to keep the files

    print()
    return (correct_count, total_count)

def compile_code(src_path, bin_path, out_path):
    cmd = Command('mkdir -p {}'.format(bin_path), './')
    cmd.run()
    cmd = Command('rm -f *.class', bin_path)
    cmd.run()
    cmd = Command('mkdir -p {}'.format(out_path), './')
    cmd.run()

    cmd = Command('javac *.java', src_path)
    sig, out, err = cmd.run()

    if sig != 0:
        print('COMPILATION ERROR')
        print('>>>>>>>> OUT >>>>>>>>')
        print(out)
        print('>>>>>>>> ERR >>>>>>>>')
        print(err)
        print('COMPILATION ERROR')
        return False

    cmd = Command('mv {}/*.class {}'.format(src_path, bin_path), './')
    cmd.run()

    return True

def parse_tests_invalid(testdir='invalid/'):
    tests = []
    outs = []
    for filename in glob.glob(testdir + '*.in'):
        tests.append(filename)

    tests.sort(key=natural_keys)
    for filename in tests:
        outname = filename.replace('.in', '.out')
        with open(outname, 'r') as f:
            outs.append(f.read().rstrip())
    return tests, outs

def run_tests_invalid(bin_path, tests, outs, char_threshold=200):
    print('#'*18, "IV", '#'*18)
    correct_count = 0
    total_count = len(tests)
    for test, expected_out in zip(tests, outs):
        cmd_string = ""
        with open(test, 'r') as file:
            cmd_string = file.readline()
            file.close()
        cmd = Command(cmd_string, '../bin/')
        sig, out, err = cmd.run()

        if sig != 0:
            print('ERR', test)
            print('>>>>>>>> ERR >>>>>>>>')
            print(err)
            continue

        if out.rstrip() == expected_out or err.rstrip() == expected_out:
            correct_count += 1
            if DISPLAY_INDIVIDUAL_RESULTS:
                print('✔', test)
        else:
            print('X', test)
            if len(out) < char_threshold and len(expected_out) < char_threshold:
                print('>>>>>>>> ERR >>>>>>>>')
                print(err)
                print('>>>>>>>> EXPECTED ERR >>>>>>>>')
                print(expected_out)

    print()
    return (correct_count, total_count)


def main():
    '''
    Modes supported:
        0 -- Greyscale
        1 -- Noise reduction
        2 -- Edge detection
        3 -- Spot detection
        4 -- Errors
        5/'all' -- All
    '''
    global DISPLAY_INDIVIDUAL_RESULTS
    if len(argv) == 2 or len(argv) == 3 or len(argv) == 5:
        mode = -1
        try:
            mode = int(argv[1])
        except Exception:
            mode = argv[1]
        src_path = SRC_PATH
        bin_path = BIN_PATH
        out_path = OUT_PATH
        if len(argv) == 3 or len(argv) == 5:
            if argv[2].lower() == 'true' or argv[2].lower() == 't':
                DISPLAY_INDIVIDUAL_RESULTS = True
            else:
                DISPLAY_INDIVIDUAL_RESULTS = False
        if len(argv) == 5:
            src_path = os.path.normpath(argv[3]) + '/'
            bin_path = os.path.normpath(argv[4]) + '/'
    else:
        print('Usage: python testscript.py <mode> [<src_dir> <bin_dir>]')
        exit()

    ALL = False
    results = {}
    if mode == 5 or str(mode).lower() == "all":
        ALL = True
    
    if compile_code(src_path, bin_path, out_path):
        if mode == 0 or ALL: # Greyscale
            tests, outs = parse_tests('gs')
            val = run_tests(bin_path, tests, outs, "gs")
            results['Greyscale'] = val
        
        if mode == 1 or ALL: # Noise reduction
            tests, outs = parse_tests('nr')
            val = run_tests(bin_path, tests, outs, "nr")
            results['Noise reduction'] = val

        if mode == 2 or ALL: # Edge Detection
            tests, outs = parse_tests('ed')
            val = run_tests(bin_path, tests, outs,"ed")
            results['Edge detection'] = val

        if mode == 3 or ALL: # Spot detection
            tests, outs = parse_tests('sd')
            val = run_tests(bin_path, tests, outs,"sd")
            results['Spot detection'] = val

        if mode == 4 or ALL:
            tests, outs = parse_tests_invalid()
            val = run_tests_invalid(bin_path, tests, outs)
            results['Invalids'] = val

    print("RESULTS:")
    for i in results.keys():
        c, t = results[i]
        print("{}: {} / {} CORRECT".format(i, c, t))

if __name__ == "__main__":
    main()