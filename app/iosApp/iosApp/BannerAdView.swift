import Foundation
import SwiftUI
import GoogleMobileAds

struct BannerAdView: UIViewRepresentable {
    
    func makeUIView(context: Context) -> GADBannerView {
        let bannerView = GADBannerView()
        
        bannerView.adUnitID = "ca-app-pub-7769337200974564/2325250126"
        
        let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene
        
        if let rootViewController = windowScene?.windows.first?.rootViewController {
            bannerView.rootViewController = rootViewController
        }
    
        bannerView.load(GADRequest())
        
        return bannerView
    }
    
    func updateUIView(_ uiView: GADBannerView, context: Context) {
        
    }
}
