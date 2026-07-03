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

    <nav class="navbar menu-navbar">
        <div class="logo">
            <i class="fa-solid fa-utensils"></i> WebDelivery
        </div>
        
        <div class="search-bar-center">
            <input type="text" id="searchInput" placeholder="Cerca il tuo piatto..." onkeyup="applicaFiltri()">
            <button type="button" onclick="applicaFiltri()"><i class="fa-solid fa-magnifying-glass"></i></button>
        </div>
        
        <div class="nav-buttons">
            <a href="carrello" class="btn btn-solid"><i class="fa-solid fa-cart-shopping"></i> Carrello</a>
            <a href="profilo" class="btn btn-icon"><i class="fa-solid fa-user"></i></a>
        </div>
    </nav>

    <div class="menu-container">
        
        <header class="menu-header">
            <#if nomeUtente?has_content>
                <h1>Ciao ${nomeUtente}!</h1>
            <#else>
                <h1>Ciao!</h1>
            </#if>
            <p>Esplora i prodotti freschi di oggi.</p>
        </header>

        <div class="category-filters">
            <button class="pill active" onclick="filtra('Tutti', this)">Tutti</button>
            <button class="pill" onclick="filtra('Pizze', this)">Pizze</button>
            <button class="pill" onclick="filtra('Burger', this)">Burger</button>
            <button class="pill" onclick="filtra('Sushi', this)">Sushi</button>
            <button class="pill" onclick="filtra('Fritti', this)">Fritti</button>
            <button class="pill" onclick="filtra('Dolci', this)">Dolci</button>
            <button class="pill" onclick="filtra('Bevande', this)">Bevande</button>
        </div>

        <div class="menu-grid">
            <#if prodotti?has_content>
                <#list prodotti as prodotto>
                    <div class="menu-card" data-categoria="${prodotto.categoria!''}">
                        
                        <div class="card-image-wrapper">
                            <#if prodotto.badge?has_content>
                                <span class="badge">${prodotto.badge}</span>
                            </#if>
                            <img src="${prodotto.immagine!'https://via.placeholder.com/600x400?text=Immagine+Non+Disponibile'}" alt="${prodotto.nome}">
                        </div>
                        
                        <div class="card-body">
                            <div class="card-title-row">
                                <h3>${prodotto.nome}</h3>
                                <span class="price">${prodotto.prezzo?string("0.00")} €</span>
                            </div>
                            
                            <p class="description">${prodotto.descrizione!''}</p>
                            
                            <div class="card-actions">
                                <button type="button" class="btn btn-outline-small" onclick="apriModal('${prodotto?index}')">
                                    <i class="fa-solid fa-sliders"></i> Personalizza
                                </button>
                                <form action="aggiungi-carrello" method="POST" style="margin: 0;">
                                    <input type="hidden" name="idProdotto" value="${prodotto.id}">
                                    <button type="submit" class="btn btn-round-solid">
                                        <i class="fa-solid fa-basket-shopping"></i>
                                    </button>
                                </form>
                            </div>
                        </div>

                    </div>
                </#list>
            <#else>
                <p style="font-size: 1.2rem; color: #666; grid-column: span 2;">
                    Al momento non ci sono prodotti disponibili nel menù.
                </p>
            </#if>
        </div>

    </div>

    <#if prodotti?has_content>
        <#list prodotti as prodotto>
            <div id="modal-menu-personalizza-${prodotto?index}" class="modal-overlay">
                <div class="modal-card">
                    <div class="modal-header">
                        <h2>Personalizza ${prodotto.nome}</h2>
                        <button type="button" class="btn-close-modal" onclick="chiudiModal('${prodotto?index}')"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                    
                    <form action="aggiungi-carrello" method="POST">
                        <input type="hidden" name="action" value="add_customized">
                        <input type="hidden" name="idProdotto" value="${prodotto.id}">
                        
                        <#assign custom = customizzazioni[prodotto.id?string]!{}>
                        
                        <#list custom?keys as nomeGruppo>
                            <div class="modal-group">
                                <label>${nomeGruppo}</label>
                                <#if nomeGruppo == "Extra">
                                    <div class="extra-options">
                                        <#list custom[nomeGruppo] as c>
                                            <label>
                                                <span><input type="checkbox" name="caratteristica" value="${c.id}" <#if c.isDefault>checked</#if>> ${c.nome}</span>
                                                <strong><#if c.differenzaPrezzo &gt; 0>+€${c.differenzaPrezzo?string("0.00")}<#elseif c.differenzaPrezzo &lt; 0>-€${(c.differenzaPrezzo * -1)?string("0.00")}<#else>€0.00</#if></strong>
                                            </label>
                                        </#list>
                                    </div>
                                <#else>
                                    <select name="caratteristica">
                                        <#list custom[nomeGruppo] as c>
                                            <option value="${c.id}" <#if c.isDefault>selected</#if>>${c.nome} (<#if c.differenzaPrezzo &gt; 0>+€${c.differenzaPrezzo?string("0.00")}<#elseif c.differenzaPrezzo &lt; 0>-€${(c.differenzaPrezzo * -1)?string("0.00")}<#else>€0.00</#if>)</option>
                                        </#list>
                                    </select>
                                </#if>
                            </div>
                        </#list>
                        
                        <button type="submit" class="btn-checkout" style="width:100%; background:#064e3b; color:white; border:none; padding:15px; border-radius:8px; font-size:1.1rem; font-weight:700; cursor:pointer;">Aggiungi e vai al Carrello</button>
                    </form>
                </div>
            </div>
        </#list>
    </#if>

<script>
        let categoriaAttuale = 'Tutti';

        function filtra(categoria, elementoBottone) {
            let bottoni = document.querySelectorAll('.pill');
            bottoni.forEach(b => b.classList.remove('active'));
            if(elementoBottone) elementoBottone.classList.add('active');
            
            categoriaAttuale = categoria;
            applicaFiltri();
        }

        function applicaFiltri() {
            let inputElement = document.getElementById('searchInput');
            if (!inputElement) return; // Se la barra non esiste, si ferma senza errori
            
            let searchTesto = inputElement.value.toLowerCase().trim();
            let cards = document.querySelectorAll('.menu-card');

            cards.forEach(card => {
                let catProdotto = (card.getAttribute('data-categoria') || '').toLowerCase().trim();
                
                let tagH3 = card.querySelector('h3');
                let tagDesc = card.querySelector('.description');
                
                let nomeProdotto = tagH3 ? tagH3.textContent.toLowerCase() : '';
                let descProdotto = tagDesc ? tagDesc.textContent.toLowerCase() : '';

                let catSelezionata = categoriaAttuale.toLowerCase();
                let matchCategoria = false;
                
                if (catSelezionata === 'tutti') {
                    matchCategoria = true;
                } else {
 
                    let radice = catSelezionata.substring(0, 4);
                    if (catProdotto.includes(radice) || catSelezionata === catProdotto) {
                        matchCategoria = true;
                    }
                }

                let matchTesto = false;
                if (searchTesto === '') {
                    matchTesto = true; // Se la barra è vuota, passa il controllo
                } else if (nomeProdotto.includes(searchTesto) || descProdotto.includes(searchTesto)) {
                    matchTesto = true; // Se il testo c'è nel titolo o nella descrizione, passa
                }

                if (matchCategoria && matchTesto) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        }
        function apriModal(index) {
            let modal = document.getElementById('modal-menu-personalizza-' + index);
            if(modal) modal.style.display = 'flex';
        }

        function chiudiModal(index) {
            let modal = document.getElementById('modal-menu-personalizza-' + index);
            if(modal) modal.style.display = 'none';
        }
    </script>
</body>
</html>