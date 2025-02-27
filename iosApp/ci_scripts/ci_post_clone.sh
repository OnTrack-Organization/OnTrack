#!/bin/sh

#  ci_post_clone.sh
#  iosApp
#
#  Created by Ashkan Haghighi Fashi on 27.02.25.
#  Copyright © 2025 orgName. All rights reserved.

echo "Setting JAVA_HOME for Xcode Cloud"
export JAVA_HOME=/Users/ashkanhaghighifashi/Library/Java/JavaVirtualMachines/corretto-21.0.6/Contents/Home
echo "JAVA_HOME set to $JAVA_HOME"

#echo "Ensuring GoogleService-Info.plist exists"
# Create the correct directory if it doesn't exist
#mkdir -p /Volumes/Desktop/iosApp/iosApp/
# Copy the file to the expected location
#cp /Users/ashkanhaghighifashi/StudioProjects/OnTrack/iosApp/iosApp/GoogleService-Info.plist /Volumes/Desktop/iosApp/iosApp/
#echo "GoogleService-Info.plist copied successfully"
