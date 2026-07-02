<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - Menù</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <div class="logo">
            <i class="fa-solid fa-utensils"></i> WebDelivery
        </div>
        <div class="nav-buttons">
            <a href="home" class="btn btn-outline">Home</a>
            <a href="carrello" class="btn btn-solid"><i class="fa-solid fa-cart-shopping"></i> Carrello</a>
        </div>
    </nav>

    <section class="popular-section">
        <h2>Il Nostro Menù</h2>
        
        <div class="cards-container" style="flex-wrap: wrap; gap: 30px; margin-top: 20px;">
            
            <#if prodotti?has_content>
                <#list prodotti as prodotto>
                    <div class="food-card" style="height: auto; padding-bottom: 15px;">
                        <img src="${prodotto.immagine!'https://via.placeholder.com/500x300?text=Cibo+Delizioso'}" alt="${prodotto.nome}">
                        
                        <div style="padding: 15px;">
                            <h3 style="font-size: 1.2rem; margin-bottom: 8px;">${prodotto.nome}</h3>
                            <p style="color: #666; font-size: 0.9rem; margin-bottom: 15px; min-height: 40px;">
                                ${prodotto.descrizione!''}
                            </p>
                            
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <strong style="color: #116C4A; font-size: 1.3rem;">€ ${prodotto.prezzo}</strong>
                                
                                <form action="aggiungi-carrello" method="POST" style="margin: 0;">
                                    <input type="hidden" name="idProdotto" value="${prodotto.id}">
                                    <button type="submit" class="btn btn-solid" style="padding: 8px 15px; font-size: 0.9rem;">
                                        Aggiungi
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </#list>
            <#else>
                <p style="font-size: 1.2rem; color: #666;">Al momento non ci sono prodotti disponibili nel menù.</p>
            </#if>

        </div>
    </section>

</body>
</html>
