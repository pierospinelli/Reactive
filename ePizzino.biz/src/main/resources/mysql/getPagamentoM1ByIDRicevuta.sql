select *
from pagamenti p
inner join debiti_pagati d on d.id_pagamento=p.id
where p.id_ricevuta=?