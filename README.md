# final

A Clojure library designed to give users a simple way to utilize customized typing practice. It offers two modes- one which forces users to type random characters, those which are typed slowest are most likely to be picked, and another which uses html code and has users type real code segments that contain characters which they struggle typing. This variety allows users to decide whether they want a more practical typing experience or more targeted solely to specific characters they find difficult.

## usage
The typing gui can be run by running the main method in the final.core namespace. Here are some of the ways you might use this library.

lein run
- this will simply run the gui without a profile in random character mode. It will not save your progress.

lein run profiles/user1.txt
- this will run the gui with a profile in random character mode. If you want to stop for the day and pick up again tomorrow, your progress is saved in the user1.txt file and you won't have to restart in order to get a customized experience. 

lein run -code
This will run the gui in coding mode without a profile. You will be given targeted code snippets rather than random characters.

lein run -code profiles/user1.txt
This will run the gui in coding mode with a profile and will save progress to the user1.txt file, allowing you to pick back up from the same spot later. 



