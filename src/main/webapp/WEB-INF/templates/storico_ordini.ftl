<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - I Miei Ordini</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { margin: 0; padding: 0; background-color: #f8f9fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .dash-container { display: flex; min-height: 100vh; }
        .dash-sidebar { width: 280px; background-color: #ffffff; border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; padding: 30px 0; }
        .dash-logo { font-size: 1.8rem; color: #116C4A; font-weight: 800; text-align: center; margin-bottom: 40px; }
        .dash-nav { list-style: none; padding: 0; margin: 0; flex-grow: 1; }
        .dash-nav li { margin-bottom: 5px; padding: 0 20px; }
        .dash-nav a { display: flex; align-items: center; gap: 15px; padding: 15px 20px; text-decoration: none; color: #64748b; font-weight: 600; border-radius: 12px; transition: 0.3s; }
        .dash-nav a i { font-size: 1.2rem; width: 20px; text-align: center; }
        .dash-nav a:hover { background-color: #f1f5f9; color: #1a202c; }
        .dash-nav a.active { background-color: #116C4A; color: #ffffff; box-shadow: 0 4px 10px rgba(17, 108, 74, 0.2); }
        .dash-nav a.danger { color: #ef4444; margin-top: auto; }
        .dash-nav a.danger:hover { background-color: #fef2f2; }
        .dash-main { flex-grow: 1; padding: 50px; overflow-y: auto; }
        .dash-header { margin-bottom: 40px; }
        .dash-header h1 { font-size: 2.2rem; color: #1a202c; margin: 0 0 10px 0; font-weight: 800; }
        .dash-header p { color: #64748b; margin: 0; font-size: 1.1rem; }
        .order-list { display: flex; flex-direction: column; gap: 20px; max-width: 800px; }
        .order-card { background: #ffffff; padding: 30px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.03); border: 1px solid #f1f5f9; }
        .order-top { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 1px solid #f1f5f9; padding-bottom: 20px; margin-bottom: 20px; }
        .order-id { margin: 0 0 5px 0; color: #1a202c; font-size: 1.3rem; font-weight: 800; }
        .order-date { color: #94a3b8; font-size: 0.9rem; margin: 0; }
        .order-price { font-size: 1.5rem; font-weight: 800; color: #116C4A; }
        .order-bottom { display: flex; justify-content: space-between; align-items: center; }
        .status-label { font-size: 0.85rem; font-weight: 700; color: #64748b; margin-bottom: 8px; display: block; text-transform: uppercase; letter-spacing: 0.5px; }
        .status-badge { display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 30px; font-size: 0.9rem; font-weight: 700; text-transform: uppercase; }
        .status-badge.inserito { background: #eff6ff; color: #3b82f6; }
        .status-badge.preparazione { background: #fef3c7; color: #d97706; }
        .status-badge.pronto { background: #f3e8ff; color: #9333ea; }
        .status-badge.consegna { background: #ecfdf5; color: #059669; }
        .status-badge.consegnato { background: #f1f5f9; color: #64748b; }
        .status-badge.annullato { background: #fef2f2; color: #ef4444; }
        .time-box { text-align: right; }
        .time-box .status-label { margin-bottom: 5px; }
        .time-value { color: #1a202c; font-size: 1.3rem; font-weight: 800; display: flex; align-items: center; gap: 8px; justify-content: flex-end; }
        .empty-state { text-align: center; padding: 60px 20px; background: #ffffff; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.03); max-width: 800px; }
        .empty-state i { font-size: 4rem; color: #cbd5e1; margin-bottom: 20px; }
        .empty-state h3 { font-size: 1.5rem; color: #1a202c; margin: 0 0 10px 0; }
        .empty-state p { color: #64748b; margin: 0 0 30px 0; }
        .btn-link { background-color: #116C4A; color: white; border: none; padding: 14px 30px; border-radius: 10px; font-size: 1.05rem; font-weight: 700; cursor: pointer; text-decoration: none; display: inline-block; transition: 0.3s; }
        .btn-link:hover { background-color: #064e3b; transform: translateY(-2px); }
    </style>
</head>
<body>
    <div class="dash-container">
        <aside class="dash-sidebar">
            <div class="dash-logo"><i class="fa-solid fa-utensils"></i> WebDelivery</div>
            <ul class="dash-nav">
                <li><a href="menu"><i class="fa-solid fa-arrow-left"></i> Torna al Menù</a></li>
                <li><a href="profilo"><i class="fa-solid fa-user"></i> Il mio Profilo</a></li>
                <li><a href="storico-ordini" class="active"><i class="fa-solid fa-clock-rotate-left"></i> I Miei Ordini</a></li>
            </ul>
            <ul class="dash-nav" style="flex-grow: 0;">
                <li><a href="logout" class="danger"><i class="fa-solid fa-right-from-bracket"></i> Esci dall'account</a></li>
            </ul>
        </aside>

        <main class="dash-main">
            <header class="dash-header">
                <h1>I Miei Ordini</h1>
                <p>Monitora lo stato delle tue consegne o consulta lo storico passato.</p>
            </header>

            <div class="order-list">
                <#if ordini?has_content>
                    <#list ordini as ordine>
                        <div class="order-card">
                            <div class="order-top">
                                <div>
                                    <h3 class="order-id">Ordine #${ordine[0]}</h3>
                                    <p class="order-date">Effettuato il ${ordine[1]?string('dd/MM/yyyy HH:mm')}</p>
                                </div>
                                <div class="order-price">€${ordine[3]?string("0.00")}</div>
                            </div>
                            
                            <div class="order-bottom">
                                <div>
                                    <span class="status-label">Stato Attuale</span>
                                    <span class="status-badge 
                                        <#if ordine[2] == 'inserito'>inserito
                                        <#elseif ordine[2] == 'in preparazione'>preparazione
                                        <#elseif ordine[2] == 'pronto'>pronto
                                        <#elseif ordine[2] == 'in consegna'>consegna
                                        <#elseif ordine[2] == 'consegnato'>consegnato
                                        <#elseif ordine[2] == 'annullato'>annullato</#if>">
                                        <#if ordine[2] == 'inserito'><i class="fa-solid fa-file-invoice"></i>
                                        <#elseif ordine[2] == 'in preparazione'><i class="fa-solid fa-fire-burner"></i>
                                        <#elseif ordine[2] == 'pronto'><i class="fa-solid fa-bell-concierge"></i>
                                        <#elseif ordine[2] == 'in consegna'><i class="fa-solid fa-motorcycle"></i>
                                        <#elseif ordine[2] == 'consegnato'><i class="fa-solid fa-check"></i>
                                        <#elseif ordine[2] == 'annullato'><i class="fa-solid fa-xmark"></i></#if>
                                        ${ordine[2]}
                                    </span>
                                </div>
                                
                                <#if ordine[2] != 'consegnato' && ordine[2] != 'annullato'>
                                    <div class="time-box">
                                        <span class="status-label">Tempo Stimato</span>
                                        <div class="time-value"><i class="fa-regular fa-clock" style="color: #116C4A;"></i> ~${ordine[4]} min</div>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </#list>
                <#else>
                    <div class="empty-state">
                        <i class="fa-solid fa-receipt"></i>
                        <h3>Nessun ordine trovato</h3>
                        <p>Il tuo storico è vuoto. È il momento perfetto per provare qualcosa di nuovo!</p>
                        <a href="menu" class="btn-link">Esplora il Menù</a>
                    </div>
                </#if>
            </div>
        </main>
    </div>
</body>
</html>