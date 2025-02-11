# For checking on Mac
for i in {0..8}; do
    echo "Running test${i}..."
    java Program "tests/test${i}.minc" > temp_output.txt 2>/dev/null
    if cmp -s temp_output.txt "solutions/testsolu${i}.txt"; then
        echo "Test${i}: PASS"
    else
        echo "Test${i}: FAIL"
    fi
    rm -f temp_output.txt
done
