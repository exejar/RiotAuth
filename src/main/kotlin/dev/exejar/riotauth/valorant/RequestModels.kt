package dev.exejar.riotauth.valorant

import kotlinx.serialization.Serializable

object WeaponSkinLevelResponse {
    @Serializable
    data class Body(
        val status: Int,
        val data: Data
    )
    @Serializable
    data class Data(
        val uuid: String,
        val displayName: String,
        val displayIcon: String,
        val streamedVideo: String?,
        val assetPath: String
    )
}

object StorefrontResponse {
    @Serializable
    data class Body(
        val featuredBundle: FeaturedBundle,
        val skinsPanelLayout: SkinsPanelLayout,
        val upgradeCurrencyStore: UpgradeCurrencyStore,
        val accessoryStore: AccessoryStore,
        val bonusStore: BonusStore? = null
    )
    @Serializable
    data class FeaturedBundle(
        val bundle: Bundle,
        val bundles: List<Bundle>,
        val bundleRemainingDurationInSeconds: Int
    )
    @Serializable
    data class Bundle(
        val ID: String? = null,
        val dataAssetID: String? = null,
        val currencyID: String? = null,
        val items: List<Items>,
        val itemOffers: List<ItemOffers>? = null,
        val totalBaseCost: Map<String, Int>? = null,
        val totalDiscountedCost: Map<String, Int>? = null,
        val totalDiscountPercent: Double,
        val durationRemainingInSeconds: Int,
        val wholesaleOnly: Boolean
    )
    @Serializable
    data class Items(
        val item: Item,
        val basePrice: Int,
        val currencyID: String? = null,
        val discountPercent: Double,
        val discountedPrice: Int,
        val isPromoItem: Boolean
    )
    @Serializable
    data class Item(
        val itemTypeID: String? = null,
        val itemID: String? = null,
        val amount: Int
    )
    @Serializable
    data class ItemOffers(
        val bundleItemOfferID: String? = null,
        val offer: Offer,
        val discountPercent: Double,
        val discountedCost: Map<String, Int>
    )
    @Serializable
    data class Offer(
        val offerID: String? = null,
        val isDirectPurchase: Boolean,
        val startDate: String,
        val cost: Map<String, Int>,
        val rewards: List<Rewards>
    )
    @Serializable
    data class Rewards(
        val itemTypeID: String? = null,
        val itemID: String? = null,
        val quantity: Int
    )
    @Serializable
    data class SkinsPanelLayout(
        val singleItemOffers: List<String>,
        val singleItemStoreOffers: List<Offer>,
        val singleItemOffersRemainingDurationInSeconds: Int
    )
    @Serializable
    data class UpgradeCurrencyStore(
        val upgradeCurrencyOffers: List<UpgradeCurrencyOffers>
    )
    @Serializable
    data class UpgradeCurrencyOffers(
        val offerID: String? = null,
        val storefrontItemID: String? = null,
        val offer: Offer,
        val discountedPercent: Double
    )
    @Serializable
    data class AccessoryStore(
        val accessoryStoreOffers: List<AccessoryStoreOffers>,
        val accessoryStoreRemainingDurationInSeconds: Int,
        val storefrontID: String? = null
    )
    @Serializable
    data class AccessoryStoreOffers(
        val offer: Offer,
        val contractID: String? = null
    )
    @Serializable
    data class BonusStore(
        val bonusStoreOffers: List<BonusStoreOffers>,
        val bonusStoreRemainingDurationInSeconds: Int
    )
    @Serializable
    data class BonusStoreOffers(
        val bonusOfferID: String? = null,
        val offer: Offer,
        val discountPercent: Double,
        val discountCosts: Map<String, Int>,
        val isSeen: Boolean
    )
}