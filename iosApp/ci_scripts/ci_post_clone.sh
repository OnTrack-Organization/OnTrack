#!/bin/sh

#  ci_post_clone.sh
#  iosApp
#
#  Created by Ashkan Haghighi Fashi on 27.02.25.
#  Copyright © 2025 orgName. All rights reserved.
echo "Ensuring GoogleService-Info.plist exists"

# Create the correct directory if it doesn't exist
mkdir -p /Volumes/Desktop/iosApp/iosApp/

# Copy the file to the expected location
cp /Users/ashkanhaghighifashi/StudioProjects/OnTrack/iosApp/iosApp/GoogleService-Info.plist /Volumes/Desktop/iosApp/iosApp/

echo "GoogleService-Info.plist copied successfully"
