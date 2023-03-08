--USERS
insert into users (id, user_id, email,first_name,last_name, password)
values ('ba804cb9-fa14-42a5-afaf-be488742fc54', 'ba804cb9-fa14-42a5-afaf-be488742fc54', 'admin@example.com', 'James','Bond', '1234' ),
('0d8fa44c-54fd-4cd0-ace9-2a7da57992de', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'user@example.com', 'Tyler','Durden', '1234')
 ON CONFLICT DO NOTHING;


--ROLES
INSERT INTO role(id, name)
VALUES ('d29e709c-0ff1-4f4c-a7ef-09f656c390f1', 'DEFAULT'),
('ab505c92-7280-49fd-a7de-258e618df074', 'USER_MODIFY'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', 'USER_DELETE'),
('90e75cb9-9a02-40c3-adb8-4b641af66bee', 'ADMIN')
ON CONFLICT DO NOTHING;

--AUTHORITIES
INSERT INTO authority(id, name)
VALUES ('2ebf301e-6c61-4076-98e3-2a38b31daf86', 'DEFAULT'),
('76d2cbf6-5845-470e-ad5f-2edb9e09a868', 'USER_MODIFY'),
('21c942db-a275-43f8-bdd6-d048c21bf5ab', 'USER_DELETE'),
('482c7a70-8ea5-4644-a301-622dd2bc0125', 'ADMIN_READ'),
('3851da68-7c7f-4584-9f99-5a5a9cea6e14', 'ADMIN_MODIFY')
ON CONFLICT DO NOTHING;

--assign roles to users
insert into users_role (users_id, role_id)
values ('0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'd29e709c-0ff1-4f4c-a7ef-09f656c390f1'),
       ('ba804cb9-fa14-42a5-afaf-be488742fc54', '90e75cb9-9a02-40c3-adb8-4b641af66bee')
 ON CONFLICT DO NOTHING;

--assign authorities to roles
INSERT INTO role_authority(role_id, authority_id)
VALUES ('d29e709c-0ff1-4f4c-a7ef-09f656c390f1', '2ebf301e-6c61-4076-98e3-2a38b31daf86'),
('ab505c92-7280-49fd-a7de-258e618df074', '76d2cbf6-5845-470e-ad5f-2edb9e09a868'),
('c6aee32d-8c35-4481-8b3e-a876a39b0c02', '21c942db-a275-43f8-bdd6-d048c21bf5ab'),
('90e75cb9-9a02-40c3-adb8-4b641af66bee', '2ebf301e-6c61-4076-98e3-2a38b31daf86'),
('90e75cb9-9a02-40c3-adb8-4b641af66bee', '76d2cbf6-5845-470e-ad5f-2edb9e09a868'),
('90e75cb9-9a02-40c3-adb8-4b641af66bee', '21c942db-a275-43f8-bdd6-d048c21bf5ab'),
('90e75cb9-9a02-40c3-adb8-4b641af66bee', '482c7a70-8ea5-4644-a301-622dd2bc0125'),
('90e75cb9-9a02-40c3-adb8-4b641af66bee', '3851da68-7c7f-4584-9f99-5a5a9cea6e14')
 ON CONFLICT DO NOTHING;

-- Create events

INSERT INTO
    events (id, description, end_date, event_name, location, start_date, event_owner_id)
VALUES
    ('300f6ca9-940e-455c-b54e-6ab415cb405e',
     'A fun late night dinner party with friends; wine and music.',
     '2022-12-12',
     '1 Dinner party with friends!',
     'EURB Zurich', '2022-12-12', 'ba804cb9-fa14-42a5-afaf-be488742fc54'),
    ('d1cc3232-9e80-4c0d-87c9-e2c36ae8a3c2',
     'Another one fun late night dinner party with friends; wine and music.',
     '2022-12-13',
     '2 Dinner party with friends!',
     'EURB Zurich', '2022-12-13', 'ba804cb9-fa14-42a5-afaf-be488742fc54'),
    ('9a877790-09fa-4f5c-bd99-05726b210938',
     'Last but not least :) A fun late night dinner party with friends; wine and music.',
     '2022-12-14',
     '3 Dinner party with friends!',
     'EURB Zurich', '2022-12-14', 'ba804cb9-fa14-42a5-afaf-be488742fc54'),
    ('76e57334-5547-4024-b4ee-db19bee7779b',
     'Last but not least :) A fun late night dinner party with friends; wine and music.',
     '2022-12-14',
     '3 Dinner party with friends!',
     'EURB Zurich', '2022-12-14', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de');

INSERT INTO
    event_user (event_id, user_id, id)
VALUES
    ('300f6ca9-940e-455c-b54e-6ab415cb405e', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de', '18d4f349-7806-4e1c-a480-2bf9ee074827'),
    ('d1cc3232-9e80-4c0d-87c9-e2c36ae8a3c2', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de', '0309992f-5338-4d62-bd9b-1483aeb84e3d'),
    ('9a877790-09fa-4f5c-bd99-05726b210938', '0d8fa44c-54fd-4cd0-ace9-2a7da57992de', 'a84f8e62-f5d3-46a8-9a41-84684546f6b9');

DELETE FROM gorse_items WHERE true;

INSERT INTO gorse_items
(item_id, is_hidden, categories, time_stamp, labels, comment)
VALUES
    ('300f6ca9-940e-455c-b54e-6ab415cb405e', false, '["Fun", "Lovely", "Categories"]', '2023-02-26 04:03:28.000000 +00:00', '["Fun", "Lovely", "Categories"]', ''),
    ('d1cc3232-9e80-4c0d-87c9-e2c36ae8a3c2', false, '["Fun", "Lovely", "Categories"]', '2023-02-26 04:03:28.000000 +00:00', '["Fun", "Lovely", "Categories"]', ''),
    ('9a877790-09fa-4f5c-bd99-05726b210938', false, '["Fun", "Lovely", "Categories"]', '2023-02-26 04:03:28.000000 +00:00', '["Fun", "Lovely", "Categories"]', ''),
    ('76e57334-5547-4024-b4ee-db19bee7779b', false, '["Fun", "Lovely", "Categories"]', '2023-02-26 04:03:28.000000 +00:00', '["Fun", "Lovely", "Categories"]', '');