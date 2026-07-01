<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - Home</title>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <div class="logo">
            <i class="fa-solid fa-utensils"></i> WebDelivery
        </div>
        
        <div class="search-bar">
            <input type="text" placeholder="Cerca piatti o ingredienti...">
            <button type="submit"><i class="fa-solid fa-magnifying-glass"></i></button>
        </div>
        
        <div class="nav-buttons">
            <a href="login" class="btn btn-outline">Accedi</a>
            <a href="registrazione" class="btn btn-solid">Registrati</a>
        </div>
    </nav>

    <header class="hero">
        <div class="hero-content">
            <h1>Il tuo cibo preferito, a casa tua.</h1>
            <ul class="hero-features">
                <li><i class="fa-solid fa-circle-check"></i> Esplora il nostro menu locale</li>
                <li><i class="fa-solid fa-circle-check"></i> Personalizza ogni dettaglio del tuo piatto</li>
                <li><i class="fa-solid fa-circle-check"></i> Ricevi l'ordine caldo in pochi minuti</li>
            </ul>
        </div>
    </header>

    <section class="popular-section">
        <h2>I Più Scelti</h2>
        <div class="cards-container">
            <div class="food-card">
                <img src="https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500" alt="Pizza">
            </div>
            <div class="food-card">
                <img src="https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500" alt="Burger">
            </div>
            <div class="food-card">
                <img src="https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500" alt="Sushi">
            </div>
        </div>
    </section>

</body>
</html>