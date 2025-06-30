```mermaid
erDiagram
    User {
        VARCHAR(36) id PK
        VARCHAR(100) username UK
        VARCHAR fullName
        VARCHAR email UK
        VARCHAR passwordHash
        VARCHAR(20) phoneNumber UK
        UserRole role
        UserStatus status
        OffsetDateTime suspendedUntil
        OffsetDateTime createdAt
        OffsetDateTime updatedAt
    }

    Cart {
        VARCHAR(36) id PK
        VARCHAR(36) customer_id FK
        OffsetDateTime createdAt
        OffsetDateTime updatedAt
    }

    CartItem {
        VARCHAR(36) id PK
        VARCHAR(36) cart_id FK
        VARCHAR(36) menu_item_id FK
        Integer quantity
    }

    MenuItem {
        VARCHAR(36) id PK
        VARCHAR(36) seller_profile_id FK
        VARCHAR name
        TEXT description
        VARCHAR imageUrl
        BigDecimal originalPrice
        BigDecimal discountedPrice
        Integer quantityAvailable
        OffsetDateTime displayStartTime
        OffsetDateTime displayEndTime
        ListingStatus status
        BigDecimal averageRating
        OffsetDateTime createdAt
        OffsetDateTime updatedAt
    }

    MenuItemReview {
        VARCHAR(36) id PK
        VARCHAR(36) customer_id FK
        VARCHAR(36) menu_item_id FK
        Integer rating
        TEXT comment
        OffsetDateTime createdAt
    }

    Order {
        VARCHAR(36) id PK
        VARCHAR(36) customer_id FK
        VARCHAR(36) seller_profile_id FK
        BigDecimal totalAmount
        OrderStatus orderStatus
        VARCHAR(6) verificationCode UK
        TEXT notes
        OffsetDateTime createdAt
        OffsetDateTime updatedAt
    }

    OrderItem {
        VARCHAR(36) id PK
        VARCHAR(36) order_id FK
        VARCHAR(36) menu_item_id FK
        Integer quantityPurchased
        BigDecimal pricePerItem
    }

    Payment {
        VARCHAR(36) id PK
        VARCHAR(36) order_id FK
        VARCHAR midtransTransactionId UK
        VARCHAR(50) paymentType
        BigDecimal amount
        PaymentStatus status
        OffsetDateTime transactionTime
        OffsetDateTime createdAt
    }

    SellerProfile {
        VARCHAR(36) id PK
        VARCHAR(36) user_id FK UK
        VARCHAR storeName
        TEXT storeDescription
        TEXT address
        BigDecimal latitude
        BigDecimal longitude
        BigDecimal balance
        OffsetDateTime createdAt
        OffsetDateTime updatedAt
    }

    WithdrawalRequest {
        VARCHAR(36) id PK
        VARCHAR(36) seller_id FK
        BigDecimal amount
        WithdrawalStatus status
        OffsetDateTime requestDate
        OffsetDateTime processedDate
        VARCHAR(36) processed_by_id FK
        VARCHAR proofOfPaymentUrl
    }

    User ||--o{ Cart : customer_id
    Cart ||--o{ CartItem : cart_id
    MenuItem ||--o{ CartItem : menu_item_id
    SellerProfile ||--o{ MenuItem : seller_profile_id
    User ||--o{ MenuItemReview : customer_id
    MenuItem ||--o{ MenuItemReview : menu_item_id
    User ||--o{ Order : customer_id
    SellerProfile ||--o{ Order : seller_profile_id
    Order ||--o{ OrderItem : order_id
    MenuItem ||--o{ OrderItem : menu_item_id
    Order ||--o{ Payment : order_id
    User ||--o{ SellerProfile : user_id
    SellerProfile ||--o{ WithdrawalRequest : seller_id
    User ||--o{ WithdrawalRequest : processed_by_id
```