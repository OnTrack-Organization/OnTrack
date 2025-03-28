#!/bin/sh

#  ci_post_clone.sh
#  iosApp
#
#  Created by Ashkan Haghighi Fashi on 27.02.25.
#  Copyright © 2025 orgName. All rights reserved.

#echo "Setting JAVA_HOME for Xcode Cloud"
#export JAVA_HOME=/Users/ashkanhaghighifashi/Library/Java/JavaVirtualMachines/corretto-21.0.6/Contents/Home
#echo "JAVA_HOME set to $JAVA_HOME"

#export JAVA_HOME=$(/usr/libexec/java_home)
#echo "JAVA_HOME set to $JAVA_HOME"

# brew install openjdk@17

echo "Setting JAVA_HOME for Xcode Cloud :)"
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.10-tem
