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
        
        <form action="menu" method="GET" class="search-bar-center">
            <input type="text" name="search" placeholder="Cerca il tuo piatto..." value="${searchParam!''}">
            <input type="hidden" name="categoria" value="${categoriaParam!'Tutti'}">
            <button type="submit"><i class="fa-solid fa-magnifying-glass"></i></button>
        </form>
        
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

        <div class="category-filters" style="display: flex; gap: 15px; margin-bottom: 30px; overflow-x: auto;">
            <a href="menu?categoria=Tutti&search=${searchParam!''}" class="pill <#if (categoriaParam!'Tutti') == 'Tutti'>active</#if>">Tutti</a>
            <a href="menu?categoria=Pizze&search=${searchParam!''}" class="pill <#if (categoriaParam!'') == 'Pizze'>active</#if>">Pizze</a>
            <a href="menu?categoria=Burger&search=${searchParam!''}" class="pill <#if (categoriaParam!'') == 'Burger'>active</#if>">Burger</a>
            <a href="menu?categoria=Sushi&search=${searchParam!''}" class="pill <#if (categoriaParam!'') == 'Sushi'>active</#if>">Sushi</a>
            <a href="menu?categoria=Fritti&search=${searchParam!''}" class="pill <#if (categoriaParam!'') == 'Fritti'>active</#if>">Fritti</a>
            <a href="menu?categoria=Dolci&search=${searchParam!''}" class="pill <#if (categoriaParam!'') == 'Dolci'>active</#if>">Dolci</a>
            <a href="menu?categoria=Bevande&search=${searchParam!''}" class="pill <#if (categoriaParam!'') == 'Bevande'>active</#if>">Bevande</a>
        </div>

        <div class="menu-grid">
            <#if prodotti?has_content>
                <#list prodotti as prodotto>
                    <div class="menu-card">
                        
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
                    Al momento non ci sono prodotti disponibili nel menù per questa ricerca.
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
        function apriModal(index) {
            document.getElementById('modal-menu-personalizza-' + index).style.display = 'flex';
        }

        function chiudiModal(index) {
            document.getElementById('modal-menu-personalizza-' + index).style.display = 'none';
        }
    </script>

</body>
</html>