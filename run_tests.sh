#!/bin/bash
# Loop through tests 0 to 8
for i in {0..8}; do
    echo "Running test${i}..."
    
    # Run the program using tests/test${i}.minc, redirecting output and errors appropriately.
    # Adjust the java command/path if needed.
    java Program "tests/test${i}.minc" > temp_output.txt 2>/dev/null

    # Compare the temporary output with the expected solution file in the solutions folder.
    # 'cmp -s' performs a silent binary comparison.
    if cmp -s temp_output.txt "solutions/testsolu${i}.txt"; then
        echo "Test${i}: PASS"
    else
        echo "Test${i}: FAIL"
    fi

    # Delete the temporary file
    rm -f temp_output.txt
done
