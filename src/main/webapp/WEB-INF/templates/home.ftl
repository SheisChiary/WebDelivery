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
        <h2>I Nostri Piatti</h2>
<div class="cards-container" style="flex-wrap: wrap; gap: 30px; margin-top: 20px;">
            
            <#if prodotti?has_content>
                <#list prodotti as prodotto>
                    <div class="food-card" style="height: auto; padding-bottom: 15px;">
                        
                        <img src="${prodotto.immagine}" alt="${prodotto.nome}">
                        
                        <div style="padding: 15px;">
                            <h3 style="font-size: 1.2rem; margin-bottom: 8px;">${prodotto.nome}</h3>
                            <p style="color: #666; font-size: 0.9rem; margin-bottom: 15px;">
                                ${prodotto.descrizione!''}
                            </p>
                            
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <strong style="color: #116C4A; font-size: 1.3rem;">€ ${prodotto.prezzo}</strong>
                                
                                <a href="login" class="btn btn-solid" style="padding: 8px 15px; font-size: 0.9rem; text-decoration: none;">
                                    Accedi per ordinare
                                </a>
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