import SwiftUI
import GoogleSignIn
import FirebaseCore
import FirebaseMessaging
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
   var body: some Scene {
      WindowGroup {
            ContentView()
              .ignoresSafeArea()
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
    
    override init() {
        ModuleKt.doInitKoin(appDeclaration: {_ in})
        
        FirebaseApp.configure()
        
        NotificationInitKt.notificationInit()
        
        NotifierManager.shared.initialize(
            configuration: NotificationPlatformConfigurationIos(
                    showPushNotification: true,
                    askNotificationPermissionOnStart: true,
                    notificationSoundName: "default")
        )
        }
    
    func application(
      _ app: UIApplication,
      open url: URL, 
      options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
      var handled: Bool

      handled = GIDSignIn.sharedInstance.handle(url)
        
      if handled {
        return true
      }

      return false
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
            Messaging.messaging().apnsToken = deviceToken
      }
}
