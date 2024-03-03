select c.name, count(p.name) as employees_number
from person p join company c
on p.company_id = c.id group by c.name
having count(p.name) =
    (select max(count) from
        (select count(person.name) from person group by person.company_id)
    )
;