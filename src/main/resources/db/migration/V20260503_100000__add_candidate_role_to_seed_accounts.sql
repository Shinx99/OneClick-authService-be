-- Migration: Add candidate role to seed candidate accounts
-- Created: Sun May 03 10:00:00 +07 2026
-- Author: Josep
-- Description: Gán vai trò 'candidate' cho các tài khoản ứng viên mẫu

INSERT INTO auth_accounts_roles (account_id, role_id)
SELECT a.account_id, r.role_id
FROM auth_accounts a
         JOIN auth_roles r ON r.role_name = 'candidate'
WHERE a.email IN (
                  'nguyen.van.an@gmail.com',
                  'tran.thi.bich@gmail.com',
                  'le.minh.duc@gmail.com',
                  'pham.thi.huong@gmail.com',
                  'hoang.van.khai@gmail.com',
                  'vo.thi.lan@gmail.com',
                  'dang.quoc.minh@gmail.com',
                  'bui.thi.ngoc@gmail.com',
                  'nguyen.hoang.phuc@gmail.com',
                  'tran.van.quang@gmail.com',
                  'ly.thi.sau@gmail.com',
                  'dinh.van.tam@gmail.com',
                  'ngo.thi.uyen@gmail.com',
                  'truong.van.vinh@gmail.com',
                  'mai.thi.xuan@gmail.com',
                  'cao.van.yen@gmail.com',
                  'luu.thi.zung@gmail.com',
                  'ha.van.bach@gmail.com',
                  'phan.thi.cam@gmail.com',
                  'kieu.van.dong@gmail.com'
    )
    ON CONFLICT (account_id, role_id) DO NOTHING;