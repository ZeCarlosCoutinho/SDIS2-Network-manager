#!/bin/bash
cd .. || exit
java -cp bin NetworkManager join_group 3001 localhost 3001 localhost 3000
cd scripts
echo "Press any key to continue . . ."
read -r var
