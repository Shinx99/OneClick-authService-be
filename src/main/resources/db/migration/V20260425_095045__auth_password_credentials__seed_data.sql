-- Migration: auth_password_credentials - seed_data
-- Created: Sat Apr 25 09:50:45 AM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
INSERT INTO auth_password_credentials (account_id, password_hash, password_algo, password_updated_at)
SELECT a.account_id, v.password_hash, v.password_algo, now()
FROM auth_accounts a
JOIN (VALUES
    ('nguyen.van.an@gmail.com',      crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('tran.thi.bich@gmail.com',      crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('le.minh.duc@gmail.com',        crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('pham.thi.huong@gmail.com',     crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('hoang.van.khai@gmail.com',     crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('vo.thi.lan@gmail.com',         crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('dang.quoc.minh@gmail.com',     crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('bui.thi.ngoc@gmail.com',       crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('nguyen.hoang.phuc@gmail.com',  crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('tran.van.quang@gmail.com',     crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('ly.thi.sau@gmail.com',         crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('dinh.van.tam@gmail.com',       crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('ngo.thi.uyen@gmail.com',       crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('truong.van.vinh@gmail.com',    crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('mai.thi.xuan@gmail.com',       crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('cao.van.yen@gmail.com',        crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('luu.thi.zung@gmail.com',       crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('ha.van.bach@gmail.com',        crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('phan.thi.cam@gmail.com',       crypt('User@123', gen_salt('bf', 10)), 'bcrypt'),
    ('kieu.van.dong@gmail.com',      crypt('User@123', gen_salt('bf', 10)), 'bcrypt')
) AS v(email, password_hash, password_algo)
    ON v.email = a.email
ON CONFLICT (account_id) DO UPDATE SET
    password_hash        = EXCLUDED.password_hash,
    password_algo        = EXCLUDED.password_algo,
    password_updated_at  = now();

