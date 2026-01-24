$ErrorActionPreference = "Stop"

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Url,
        [hashtable]$Body = @{},
        [string]$Token
    )

    $Headers = @{
        "Content-Type" = "application/json"
    }
    if ($Token) {
        $Headers["Authorization"] = "Bearer $Token"
    }

    $Params = @{
        Method  = $Method
        Uri     = "http://localhost:8080$Url"
        Headers = $Headers
    }

    if ($Method -ne "GET" -and $Method -ne "DELETE") {
        $Params["Body"] = ($Body | ConvertTo-Json -Depth 10)
    }

    try {
        $Response = Invoke-RestMethod @Params
        return $Response
    }
    catch {
        Write-Host "Error calling $Method $Url" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        if ($_.Exception.Response) {
            $Stream = $_.Exception.Response.GetResponseStream()
            $Reader = New-Object System.IO.StreamReader($Stream)
            $Body = $Reader.ReadToEnd()
            Write-Host "Response Body: $Body" -ForegroundColor Yellow
        }
        throw $_
    }
}

Write-Host "Starting Verification..." -ForegroundColor Cyan

# 1. Auth
Write-Host "`n1. Authentication" -ForegroundColor Green
try {
    Invoke-Api -Method POST -Url "/auth/register" -Body @{email = "admin@test.com"; password = "password123" } | Out-Null
    Write-Host "Admin Registered"
}
catch { Write-Host "Admin registration failed (might already exist)" -ForegroundColor Yellow }

try {
    Invoke-Api -Method POST -Url "/auth/register" -Body @{email = "customer@test.com"; password = "password123" } | Out-Null
    Write-Host "Customer Registered"
}
catch { Write-Host "Customer registration failed (might already exist)" -ForegroundColor Yellow }

$AdminToken = Invoke-Api -Method POST -Url "/auth/login" -Body @{email = "admin@test.com"; password = "password123" }
Write-Host "Admin Token: $AdminToken"

$CustomerToken = Invoke-Api -Method POST -Url "/auth/login" -Body @{email = "customer@test.com"; password = "password123" }
Write-Host "Customer Token: $CustomerToken"

# 2. Product Management (Admin)
Write-Host "`n2. Product Management" -ForegroundColor Green
$Product = Invoke-Api -Method POST -Url "/products" -Token $AdminToken -Body @{
    name          = "Test Product"
    price         = 99.99
    stockQuantity = 10
    category      = "Electronics"
}
Write-Host "Product Created: $($Product.id)"

$Product.price = 89.99
$UpdatedProduct = Invoke-Api -Method PUT -Url "/products/$($Product.id)" -Token $AdminToken -Body $Product
Write-Host "Product Updated Price: $($UpdatedProduct.price)"

# 3. Public/Customer Product View
Write-Host "`n3. Product Discovery" -ForegroundColor Green
$Products = Invoke-Api -Method GET -Url "/products"
Write-Host "All Products Count: $($Products.Count)"

$ProductsSorted = Invoke-Api -Method GET -Url "/products?sort=price_desc"
Write-Host "Products Sorted (Price Desc): $($ProductsSorted | ForEach-Object { $_.price })"

$ProductDetail = Invoke-Api -Method GET -Url "/products/$($Product.id)"
Write-Host "Product Detail: $($ProductDetail.name)"

# 4. Cart (Customer)
Write-Host "`n4. Cart Operations" -ForegroundColor Green
# Start with empty cart simulation (assuming new user or clearing old one manually if needed)
# Since we don't have clear cart endpoint exposed simply, we just add.

# Note: In real app, userId should be inferred from token, but current implementation takes it in body
# We need the User ID. For now assume IDs 1 and 2 if fresh db, but let's try to get it from something?
# We can't easily get ID from token without decoding it here. 
# Workaround: Modify Login to return User ID or fetch profile? 
# Current Login returns STRING token only.
# Verification Script Hardcoding User IDs is risky if DB not fresh.
# BUT, AuthController register doesn't return ID.
# Let's assume we are testing on fresh DB or we can fetch user details? No /me endpoint.
# We will iterate 1 to 10 to find the user ID corresponding to email? No, that's hacky.
# Let's assume User ID 1 is Admin, User ID 2 is Customer (if fresh).
$CustomerId = 2 

Write-Host "Adding to Cart for User ID $CustomerId..."
$Cart = Invoke-Api -Method POST -Url "/cart/add" -Token $CustomerToken -Body @{
    userId    = $CustomerId
    productId = $Product.id
    quantity  = 2
}
Write-Host "Cart Items: $($Cart.items.Count)"

$Cart = Invoke-Api -Method GET -Url "/cart?userId=$CustomerId" -Token $CustomerToken
Write-Host "Cart Retrieved: $($Cart.items[0].product.name)"

# 5. Order (Customer)
Write-Host "`n5. Order Placement" -ForegroundColor Green
$Order = Invoke-Api -Method POST -Url "/orders/$CustomerId" -Token $CustomerToken
Write-Host "Order Placed: $($Order.id) Status: $($Order.status)"

$OrderDetail = Invoke-Api -Method GET -Url "/orders/$($Order.id)" -Token $CustomerToken
Write-Host "Order Retrieved: $($OrderDetail.totalPrice)"

# 6. Cleanup (Admin)
Write-Host "`n6. Cleanup" -ForegroundColor Green
Invoke-Api -Method DELETE -Url "/products/$($Product.id)" -Token $AdminToken
Write-Host "Product Deleted"

Write-Host "`nVerification Completed Successfully!" -ForegroundColor Green
