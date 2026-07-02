<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - Carrello</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="cart-page">

    <nav class="navbar cart-navbar">
        <div class="logo">
            <i class="fa-solid fa-utensils"></i> WebDelivery
        </div>
        <div class="nav-back">
            <a href="menu"><i class="fa-solid fa-arrow-left"></i> Torna al Menù</a>
            <a href="profilo" class="btn-icon"><i class="fa-solid fa-user"></i></a>
        </div>
    </nav>

    <div class="cart-container">
        <header class="cart-header">
            <h1>Completa il tuo Ordine</h1>
            <p>Verifica i tuoi piatti e seleziona i dettagli della consegna.</p>
        </header>

        <div class="cart-layout">
            <div class="cart-left">
                <h2><i class="fa-solid fa-basket-shopping"></i> Il tuo Carrello</h2>

                <#if carrello?has_content>
                    <div class="cart-items-list">
                        <#list carrello as item>
                            <div class="cart-item-card">
                                <img src="${item.prodotto.immagine!''}" alt="${item.prodotto.nome}">
                                <div class="item-details">
                                    <div class="item-title-row">
                                        <h3>${item.prodotto.nome}</h3>
                                        <span class="item-price">€${(item.prodotto.prezzo + item.costoExtra)?string("0.00")}</span>
                                    </div>
                                    <p class="item-desc">${item.prodotto.descrizione!''}</p>
                                    
                                    <#if item.personalizzazioni?has_content>
                                        <p style="color: #116C4A; font-size: 0.85rem; margin-top: -10px; margin-bottom: 15px;"><strong>Scelte:</strong> ${item.personalizzazioni}</p>
                                    </#if>
                                    
                                    <div class="item-actions">
                                        <div class="qty-selector">
                                            <form action="carrello" method="POST">
                                                <input type="hidden" name="action" value="decrease">
                                                <input type="hidden" name="idProdotto" value="${item.prodotto.id}">
                                                <button type="submit">-</button>
                                            </form>
                                            <span>${item.quantita}</span>
                                            <form action="carrello" method="POST">
                                                <input type="hidden" name="action" value="add">
                                                <input type="hidden" name="idProdotto" value="${item.prodotto.id}">
                                                <button type="submit">+</button>
                                            </form>
                                        </div>
                                        
                                        <button type="button" class="btn-modify" onclick="apriModal('${item?index}')">
                                            <i class="fa-solid fa-sliders"></i> Modifica
                                        </button>

                                        <form action="carrello" method="POST" class="form-remove">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="idProdotto" value="${item.prodotto.id}">
                                            <button type="submit" class="btn-remove"><i class="fa-regular fa-trash-can"></i> Rimuovi</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>

                    <div class="cross-sell-section">
                        <div class="cross-sell-header">
                            <h3>Voglia di qualcosa in più?</h3>
                            <a href="menu" class="btn-cross-sell"><i class="fa-solid fa-plus"></i> Menù</a>
                        </div>
                        <div class="cross-sell-grid">
                            <#list bevande as bevanda>
                                <div class="cross-sell-card">
                                    <img src="${bevanda.immagine!''}" alt="${bevanda.nome}">
                                    <h4>${bevanda.nome}</h4>
                                    <span>€${bevanda.prezzo?string("0.00")}</span>
                                    <form action="carrello" method="POST">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="idProdotto" value="${bevanda.id}">
                                        <button type="submit">+ Aggiungi</button>
                                    </form>
                                </div>
                            </#list>
                        </div>
                    </div>

                <#else>
                    <div class="empty-cart-styled">
                        <i class="fa-solid fa-basket-shopping"></i>
                        <h3>Il tuo carrello è vuoto</h3>
                        <p>Non hai ancora aggiunto piatti al tuo ordine.</p>
                        <a href="menu" class="btn-checkout">Torna al Menù</a>
                    </div>
                </#if>
            </div>

            <div class="cart-right">
                <div class="checkout-card">
                    <h2>Dettagli Consegna</h2>
                    
                    <form action="conferma-ordine" method="POST">
                        <div class="delivery-time-section">
                            <label>Orario di consegna</label>
                            <div class="time-toggles">
                                <button type="button" class="time-btn active" onclick="impostaProgrammazione(false, this)">Il prima possibile</button>
                                <button type="button" class="time-btn" onclick="impostaProgrammazione(true, this)">Programma</button>
                            </div>
                            <input type="time" id="orario-scelto" name="orarioProgrammato" class="input-time" disabled required>
                            <span class="estimated-time"><i class="fa-regular fa-clock"></i> Tempo stimato: ${tempoMinimo}-${tempoMassimo} min</span>
                        </div>

                        <div class="notes-section">
                            <label>Note per il corriere</label>
                            <textarea name="note" placeholder="Citofono, piano..."></textarea>
                        </div>

                        <div class="totals-section">
                            <div class="total-row">
                                <span>Subtotale</span>
                                <span>€${subtotale?string("0.00")}</span>
                            </div>
                            <div class="total-row">
                                <span>Consegna</span>
                                <span>€${costoConsegna?string("0.00")}</span>
                            </div>
                            <div class="total-row grand-total">
                                <span>Totale</span>
                                <span>€${totale?string("0.00")}</span>
                            </div>
                        </div>

                        <button type="submit" class="btn-checkout" <#if !carrello?has_content>disabled</#if>>
                            Conferma e Paga <i class="fa-solid fa-arrow-right"></i>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <#if carrello?has_content>
        <#list carrello as item>
            <div id="modal-personalizza-${item?index}" class="modal-overlay">
                <div class="modal-card">
                    <div class="modal-header">
                        <h2>Personalizza ${item.prodotto.nome}</h2>
                        <button type="button" class="btn-close-modal" onclick="chiudiModal('${item?index}')"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                    
                    <form action="carrello" method="POST">
                        <input type="hidden" name="action" value="customize">
                        <input type="hidden" name="itemIndex" value="${item?index}">
                        
                        <#assign custom = customizzazioni[item.prodotto.id?string]!{}>
                        
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
                        
                        <button type="submit" class="btn-checkout">Salva Modifiche</button>
                    </form>
                </div>
            </div>
        </#list>
    </#if>

    <script>
        function impostaProgrammazione(mostra, bottoneCliccato) {
            const bottoni = document.querySelectorAll('.time-btn');
            bottoni.forEach(b => b.classList.remove('active'));
            bottoneCliccato.classList.add('active');
            
            const inputOrario = document.getElementById('orario-scelto');
            if (mostra) {
                inputOrario.style.display = 'block';
                inputOrario.disabled = false;
            } else {
                inputOrario.style.display = 'none';
                inputOrario.disabled = true;
            }
        }

        function apriModal(index) {
            document.getElementById('modal-personalizza-' + index).style.display = 'flex';
        }

        function chiudiModal(index) {
            document.getElementById('modal-personalizza-' + index).style.display = 'none';
        }
    </script>
</body>
</html>