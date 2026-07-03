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
        
        <form action="menu" method="GET" class="search-bar">
            <input type="text" name="q" placeholder="Cerca piatti o ingredienti...">
            <button type="submit"><i class="fa-solid fa-magnifying-glass"></i></button>
        </form>
        
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
        <h2>I Nostri Piatti</h2>
        <div class="cards-container">
            <#if prodotti?has_content>
                <#list prodotti as prodotto>
                    <div class="food-card">
                        <img src="${prodotto.immagine!''}" alt="${prodotto.nome}">
                        <div class="food-card-body">
                            
                            <div class="food-card-tags">
                                <span class="tag-categoria">${prodotto.categoria}</span>
                                <#if prodotto.badge?? && prodotto.badge != ''>
                                    <span class="tag-badge"><i class="fa-solid fa-star" style="font-size: 0.7rem;"></i> ${prodotto.badge}</span>
                                </#if>
                            </div>

                            <h3 class="food-card-title">${prodotto.nome}</h3>
                            <p class="food-card-desc">${prodotto.descrizione!''}</p>
                            
                            <div class="food-card-footer">
                                <span class="food-card-price">€ ${prodotto.prezzo?string("0.00")}</span>
                                <a href="login" class="btn-login-order">Accedi per ordinare</a>
                            </div>
                            
                        </div>
                    </div>
                </#list>
            <#else>
                <p>Nessun prodotto trovato nel database.</p>
            </#if>
        </div>
    </section>

</body>
</html>