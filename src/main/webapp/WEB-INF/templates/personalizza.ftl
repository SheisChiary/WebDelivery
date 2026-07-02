
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personalizza Piatto - WebDelivery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="auth-body">
    
    <header class="auth-header">
        <div class="logo"><i class="fa-solid fa-utensils"></i> WebDelivery</div>
    </header>

    <div class="auth-wrapper">
        <div class="auth-card" style="max-width: 550px;">
            <h2 class="auth-title">Personalizza</h2>
            <p class="auth-subtitle">Modifica le caratteristiche del piatto</p>
            
            <form action="salva-personalizzazione" method="POST">
                <input type="hidden" name="idProdotto" value="${id!''}">
                
                <div class="form-group">
                    <label style="font-size: 1.05rem; color: #116C4A; border-bottom: 2px solid #ecfdf5; padding-bottom: 5px;">Variante Principale</label>
                    <select class="form-control" name="gruppoEsclusivo" style="width: 100%; padding: 12px; border: 1px solid #e2e8f0; border-radius: 8px; margin-top: 10px; outline: none; cursor: pointer;">
                        <option value="1">Classico (€0.00)</option>
                        <option value="2">Integrale (+€1.50)</option>
                        <option value="3">Gluten Free (+€2.50)</option>
                    </select>
                </div>
                
                <div class="form-group" style="margin-top: 25px;">
                    <label style="font-size: 1.05rem; color: #116C4A; border-bottom: 2px solid #ecfdf5; padding-bottom: 5px; margin-bottom: 15px;">Aggiunte Extra</label>
                    
                    <div style="display: flex; flex-direction: column; gap: 15px; margin-top: 10px;">
                        <label style="display: flex; align-items: center; justify-content: space-between; font-weight: normal; cursor: pointer;">
                            <span style="display: flex; align-items: center; gap: 10px;">
                                <input type="checkbox" name="extra" value="17" style="width: 18px; height: 18px; accent-color: #116C4A;"> 
                                Doppia Mozzarella
                            </span>
                            <strong style="color: #666;">+€2.00</strong>
                        </label>

                        <label style="display: flex; align-items: center; justify-content: space-between; font-weight: normal; cursor: pointer;">
                            <span style="display: flex; align-items: center; gap: 10px;">
                                <input type="checkbox" name="extra" value="18" style="width: 18px; height: 18px; accent-color: #116C4A;"> 
                                Gorgonzola Extra
                            </span>
                            <strong style="color: #666;">+€1.50</strong>
                        </label>
                    </div>
                </div>
                
                <button type="submit" class="btn-auth" style="margin-top: 35px;">Aggiorna Piatto</button>
                <a href="carrello" style="display: block; text-align: center; margin-top: 15px; color: #94a3b8; text-decoration: none; font-weight: 600;">Annulla e Torna Indietro</a>
            </form>
        </div>
    </div>
</body>
</html>