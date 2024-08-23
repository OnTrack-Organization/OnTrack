import SwiftUI
import GoogleSignIn
import FirebaseCore

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
   var body: some Scene {
      WindowGroup {
            ContentView()
              .onOpenURL(perform: { url in
                GIDSignIn.sharedInstance.handle(url)
            })
              .onAppear {
                GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
                  // Check if `user` exists; otherwise, do something with `error`
                }
              }
      }
   }
}

// Handles Urls and if its google stuff it does stuff
class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
      _ app: UIApplication,
      open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
      var handled: Bool

      handled = GIDSignIn.sharedInstance.handle(url)
      if handled {
        return true
      }

      // Handle other custom URL types.

      // If not handled by this app, return false.
      return false
    }
}
